using Microsoft.AspNetCore.Mvc;
using ServiceLayer.SessionVariable;
using ServiceLayer.Contracts;
using ServiceLayer.DtoModels;

namespace PresentationLayer.Controllers
{
    [Route("api/[controller]")]
    public class CommentController : ControllerBase
    {
        private readonly ICommentService _commentService;
        private readonly ManagementToken _managementToken;
        private readonly IUserService _userService;

        public CommentController(ICommentService commentService, IUserService userService)
        {
            _commentService = commentService;
            _managementToken = new ManagementToken();
            _userService = userService;
        }

        [HttpGet("get/all/articleId/{id}")]
        public IActionResult GetByArticleId(int id)
        {
            var comments = _commentService.GetByArticleId(id);

            return Ok(comments);
        }

        [HttpGet("get/comment/{id}")]
        public IActionResult Get(int id)
        {
            var comment = _commentService.Get(id);
            if (comment == null)
                return NotFound();
            return Ok(comment);
        }

        [HttpDelete("get/delete/{id}")]
        public IActionResult Delete(int id)
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
                var user = _userService.Get((int)userId);
                if (user != null)
                {
                    var comm = _commentService.Get(id);
                    if (comm == null)
                    {
                        return NotFound();
                    }
                    // logic comm nu poate fi null
                    // creatori pot sterge comentarii
                    if (comm.UserId == userId || user.IsCreator == true)
                    {
                        _commentService.Delete(id);

                        return Ok();
                    }
                }
            }
            return Unauthorized();

        }

        [HttpPut("get/update/{id}")]
        public IActionResult Update(int id, [FromBody] CreateCommentDto comment)
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
                _commentService.Update(id, comment);

                return Ok();
            }
            return Unauthorized();
        }

        [HttpPut("get/insert")]
        public IActionResult Insert([FromBody] CreateCommentDto comment)
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
                comment.UserId = (int)userId;
                _commentService.Insert(comment);

                return Ok();
            }
            return Unauthorized();
        }
    }
}