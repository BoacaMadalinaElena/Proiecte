using Microsoft.AspNetCore.Mvc;
using ServiceLayer.SessionVariable;
using ServiceLayer.Contracts;
using ServiceLayer.DtoModels;

namespace PresentationLayer.Controllers
{
    [Route("api/[controller]")]
    public class FeedbackController : ControllerBase
    {
        private readonly IFeedbackService _feedbackService;
        private readonly ManagementToken _managementToken;

        public FeedbackController(IFeedbackService feedbackService)
        {
            _feedbackService = feedbackService;
            _managementToken = new ManagementToken();
        }

        [HttpGet("get/all/commentId/{id}")]
        public IActionResult GetByArticleId(int id)
        {
            var comments = _feedbackService.GetByCommentId(id);

            return Ok(comments);
        }

        [HttpGet("get/feedbacks/{id}")]
        public IActionResult Get(FeedbackDto feedIn)
        {
            var feed = _feedbackService.Get(feedIn);
            if (feed == null)
                return NotFound();
            return Ok(feed);
        }

        [HttpDelete("get/delete")]
        public IActionResult Delete([FromBody] FeedbackDto feedback)
        {
            // header-ul Authorization din request
            var authHeader = Request.Headers["Authorization"].FirstOrDefault();

            if (string.IsNullOrEmpty(authHeader))
            {
                return Unauthorized(); // Nu am token
            }

            var userId = _managementToken.IsValid(authHeader);
            var feed = _feedbackService.Get(feedback);
            if (feed == null)
            {
                return NotFound();
            }
            if (userId != null && feed.UserId == userId)
            {
                _feedbackService.Delete(feed);
                return Ok();
            }
            return Unauthorized();
        }

        [HttpPut("get/update/")]
        public IActionResult Update([FromBody] FeedbackDto feedback)
        {
            // header-ul Authorization din request
            var authHeader = Request.Headers["Authorization"].FirstOrDefault();

            if (string.IsNullOrEmpty(authHeader))
            {
                return Unauthorized(); // Nu am token
            }

            var userId = _managementToken.IsValid(authHeader);
            var feed = _feedbackService.Get(feedback);
            if (feed == null)
                return NotFound();
            if (userId != null && feed.UserId == userId)
            {
                _feedbackService.Update(feedback);
                return Ok();
            }
            return Unauthorized();
        }

        [HttpPut("get/insert")]
        public IActionResult Insert([FromBody] FeedbackDto feed)
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
                feed.UserId = (int)userId;
                _feedbackService.Insert(feed);

                return Ok();
            }
            return Unauthorized();
        }
    }
}