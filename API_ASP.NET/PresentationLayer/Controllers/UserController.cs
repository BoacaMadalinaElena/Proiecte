using Microsoft.AspNetCore.Mvc;
using ServiceLayer.SessionVariable;
using ServiceLayer.Contracts;
using ServiceLayer.DtoModels;
using ServiceLayer.Notify;
using System.Collections.Concurrent;

namespace PresentationLayer.Controllers
{
    [Route("api/[controller]")]
    public class UserController : ControllerBase
    {
        private readonly IUserService _userService;
        private readonly Email _email;

        // Dictionar sincronizat pentru a mentine codurile trimise 
        private static ConcurrentDictionary<string, int> _codeDictionary = new ConcurrentDictionary<string, int>();
        private readonly ManagementToken _managementToken;

        public UserController(IUserService userService)
        {
            _userService = userService;
            _email = new Email();
            _managementToken = new ManagementToken();
        }

        [HttpGet("get/info/")]
        public IActionResult GetInfo()
        {
            var users = _userService.GetAll();

            // Selecteazã doar utilizatorii care sunt creatori
            var infoToReturn = users.Where(user => user.IsCreator)
                                   .Select(user => new
                                   {
                                       user.Name,
                                       user.Description,
                                       user.Link,
                                       user.Img
                                   });

            return Ok(infoToReturn);
        }

        /// <summary>
        /// Utilizatori ce sunt creatori
        /// </summary>
        /// <param name="user"></param>
        /// <returns></returns>
        [HttpPost("login")]
        public IActionResult Login([FromBody] UserLoginDto user)
        {
            int? id = _userService.Login(user);

            if (id == null)
            {
                return Unauthorized();
            }
            else
            {
                // creare token
                var token = _managementToken.GetToken((int)id);
                var userName = _userService.Get((int)id).Name;
                return Ok(new { Message = "Login successful", Token = token ,Id = id,Name = userName, IsCreator  = _userService.Get((int)id).IsCreator});
            }
        }

        /// <summary>
        /// Crearea unui nou utilizator
        /// </summary>
        /// <param name="user"></param>
        /// <returns></returns>
        [HttpPost("createAcount")]
        public IActionResult Add([FromBody] UserDtoCreateAcount user)
        {
            try
            {
                _userService.Insert(user);
            }catch (InvalidOperationException ex)
            {
                return BadRequest(ex.Message);
            }
            // creare token
            var id = _userService.GetUserIdByEmail(user.Email);
            if (id != null)
            {
                var token = _managementToken.GetToken((int)id);
                return Ok(new { Message = "Create successful", Token = token, Id = id, IsCreator = user.IsCreator});
            }
            else
            {
                return BadRequest();
            }
            
        }

        [HttpPost("createAcountCreator")]
        public IActionResult AddCreator([FromBody] UserContentCreatorDto user)
        {
            try
            {
                _userService.Insert(user);
            }
            catch (InvalidOperationException ex)
            {
                return BadRequest(ex.Message);
            }
            // creare token
            var id = _userService.GetUserIdByEmail(user.Email);
            if (id != null)
            {
                var token = _managementToken.GetToken((int)id);
                return Ok(new { Message = "Create successful", Token = token ,Id = id, IsCreator  = user.IsCreator});
            }
            else
            {
                return BadRequest();
            }
        }

        /// <summary>
        /// Modificarea parolei unui utilizator se trimite parola si token-ul in header
        /// </summary>
        /// <param name="pass">Parola in body</param>
        /// <returns></returns>
        [HttpPost("changePassword")]
        public IActionResult UpdatePassword([FromBody] NewPasswordDto pass)
        {
            // header-ul Authorization din request
            var authHeader = Request.Headers["Authorization"].FirstOrDefault();

            if (string.IsNullOrEmpty(authHeader))
            {
                return Unauthorized(); // Nu am token
            }

            // Id dupã token
            var id = _managementToken.IsValid(authHeader);

            if (id == null)
            {
                return Unauthorized( ); // Token invalid sau expirat
            }
            else
            {
                _userService.UpdatePassword((int)id, pass.Password);
                return Ok();
            }
        }

        /// <summary>
        /// Metoda ce trimite codul 
        /// </summary>
        /// <returns></returns>
        [HttpPost("sendCode")]
        public IActionResult SendCode([FromBody] EmailDto emailModel)
        {
            if(emailModel == null)
            {
                return BadRequest();
            }
            string email = emailModel?.Email;
            if (string.IsNullOrEmpty(email))
            {
                return BadRequest("Invalid email data.");
            }

            int code = new Random().Next(100000, 999999);
            _codeDictionary[email] = code;
            if (_userService.GetUserIdByEmail(email) != null)
            {
                _email.Send(email, code);
            }
            else
            {
                return BadRequest("The user does not exist");
            }

            return Ok();
        }

        /// <summary>
        /// Metoda ce verifica codul trimis si genereaza token de utilizator conectat 
        /// </summary>
        /// <returns></returns>
        [HttpPost("checkCode")]
        public IActionResult CheckCode([FromBody] CheckCodeDto checkCodeDto)
        {
            var id = _userService.GetUserIdByEmail(checkCodeDto.Email);
            if (id != null)
            {
                var token = _managementToken.GetToken((int)id);
                var isCreator = _userService.Get((int)id).IsCreator;
                var codeSend = _codeDictionary[checkCodeDto.Email];
                if (codeSend == checkCodeDto.Code)
                {
                    return Ok(new { MessageMessage = "Login successful. Change password" , Token = token,Id = id , IsCreator  = isCreator });
                }
            }

            return Unauthorized( id);
        }

        [HttpPost("addToFavorites")]
        public IActionResult AddToFavorites(int articleId)
        {
            // header-ul Authorization din request
            var authHeader = Request.Headers["Authorization"].FirstOrDefault();
            Console.WriteLine(authHeader);

            if (string.IsNullOrEmpty(authHeader))
            {
                return Unauthorized(); // Nu am token
            }

            var userId = _managementToken.IsValid(authHeader);


            if (userId != null)
            {
                _userService.AddToFavorites((int)userId, articleId);
                return Ok();
            }
            else
            {
                return Unauthorized();
            }
        }

        [HttpPost("removeToFavorites")]
        public IActionResult RemoveToFavorites(int articleId)
        {
            // header-ul Authorization din request
            var authHeader = Request.Headers["Authorization"].FirstOrDefault();

            if (string.IsNullOrEmpty(authHeader))
            {
                return Unauthorized(); // Nu am token
            }

            var userId = _managementToken.IsValid(authHeader);

            if (userId != null)
            {
                _userService.RemoveToFavorites((int)userId, articleId);
                return Ok();
            }
            else
            {
                return Unauthorized();
            }
        }

        [HttpGet("User/get/authors")]
        public IActionResult GetAllAuthors()
        {
            return Ok(_userService.GetAuthors());
        }
    }
}