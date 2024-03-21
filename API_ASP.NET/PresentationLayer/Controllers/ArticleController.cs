using Microsoft.AspNetCore.Mvc;
using ServiceLayer.SessionVariable;
using ServiceLayer.Contracts;
using ServiceLayer.DtoModels;

namespace PresentationLayer.Controllers
{
    [Route("api/[controller]")]
    public class ArticleController : ControllerBase
    {
        private readonly IArticleService _articleService;
        private readonly ManagementToken _managementToken;

        public ArticleController(IArticleService articleService)
        {
            _articleService = articleService;
            _managementToken = new ManagementToken();
        }

        [HttpPost("createArticle")]
        public IActionResult Add([FromBody] CreateArticleDto article)
        {
            // header-ul Authorization din request
            var authHeader = Request.Headers["Authorization"].FirstOrDefault();
            if (string.IsNullOrEmpty(authHeader))
            {
                return Unauthorized(); // Nu am token
            }
            // Extragere token
            var token = authHeader;
            var id = _managementToken.IsValid(token);

            if (id != null)
            {
                article.PublicateDate = DateTime.Now;
                article.AuthorId = (int)id;
                _articleService.Insert(article);
                return Ok();
            }
            else
            {
                return Unauthorized();
            }
        }

        [HttpPut("updateArticle/{id}")]
        public IActionResult Update(int id, [FromBody] CreateArticleDto updatedArticle)
        {
            // header-ul Authorization din request
            var authHeader = Request.Headers["Authorization"].FirstOrDefault();

            if (string.IsNullOrEmpty(authHeader))
            {
                return Unauthorized(); // Nu am token
            }

            // Extragere token
            var token = authHeader;
            var authorId = _managementToken.IsValid(token);

            if (authorId != null)
            {
                updatedArticle.AuthorId = (int)authorId;
                //vf daca exista articolul cu id-ul id pentru a returna mesaj consecvent cu cererea 
                var oldArticle = _articleService.Get(id);
                if (oldArticle == null)
                {
                    return NotFound();
                }
                if (oldArticle.AuthorId != authorId)
                {
                    return Unauthorized();
                }

                _articleService.Update(id, updatedArticle);
                return Ok();
            }
            return Unauthorized();
        }

        [HttpGet("get/authors")]
        public IActionResult GetCount()
        {
            return Ok(_articleService.GetAutors());
        }

        [HttpDelete("deleteArticle")]
        public IActionResult Delete(int id)
        {
            // header-ul Authorization din request
            var authHeader = Request.Headers["Authorization"].FirstOrDefault();

            if (string.IsNullOrEmpty(authHeader))
            {
                return Unauthorized(); // Nu am token
            }

            // Extragere token
            var token = authHeader;
            var authorId = _managementToken.IsValid(token);

            //vf daca exista articolul cu id-ul id
            var oldArticle = _articleService.Get(id);
            if (oldArticle == null)
            {
                return NotFound();
            }
            else
            {
                if (authorId != null && oldArticle.AuthorId == authorId)
                {
                    _articleService.Delete(id);
                    return Ok();
                }
                else
                {
                    return Unauthorized();
                }
            }
        }

        [HttpGet("get/{id}")]
        public IActionResult GetById(int id)
        {
            return Ok(_articleService.Get(id));
        }

        [HttpPost("get/formatted")]
        public IActionResult GetByFormat(int page, [FromBody] FilterSortConfigDto filterSortConfigDto)
        {
            int? userId = 0;
            try
            {
                // header-ul Authorization din request
                var authHeader = Request.Headers["Authorization"].FirstOrDefault();

                var token = authHeader;
                userId = _managementToken.IsValid(token);
            }
            catch (NullReferenceException ex)
            {
                userId = 0;
            }
            catch (Exception ex)
            {
                userId = 0;
            }
            return Ok(_articleService.GetByFormat(page, filterSortConfigDto, (int)userId));

        }

        [HttpPost("get/favorites")]
        public IActionResult GetFavorites(int page, [FromBody] FilterSortConfigDto filterSortConfigDto)
        {
            // header-ul Authorization din request
            var authHeader = Request.Headers["Authorization"].FirstOrDefault();

            if (string.IsNullOrEmpty(authHeader))
            {
                return Unauthorized(); // Nu am token
            }

            var token = authHeader;
            var userId = _managementToken.IsValid(token);

            if (userId != null)
            {
                return Ok(_articleService.GetFavorites(page, (int)userId, filterSortConfigDto));
            }
            else
            {
                return Unauthorized();
            }

        }

        [HttpPost("get/byAuthorId")]
        public IActionResult GetByAuthor(int page, [FromBody] FilterSortConfigDto filterSortConfigDto)
        {
            // header-ul Authorization din request
            var authHeader = Request.Headers["Authorization"].FirstOrDefault();

            if (string.IsNullOrEmpty(authHeader))
            {
                return Unauthorized(); // Nu am token
            }

            // Extragere token
            var token = authHeader;
            var authorId = _managementToken.IsValid(token);
            if (authorId != null)
            {
                return Ok(_articleService.GetByAuthor((int)authorId, page, filterSortConfigDto));
            }
            else
            {
                return Unauthorized();
            }
        }
    }
}