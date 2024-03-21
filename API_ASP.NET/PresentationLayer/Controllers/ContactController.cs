using Microsoft.AspNetCore.Mvc;
using ServiceLayer.Contracts;
using ServiceLayer.DtoModels;

namespace PresentationLayer.Controllers
{
    [Route("api/[controller]")]
    public class ContactController : ControllerBase
    {
        private readonly IContactService _contactService;

        public ContactController([FromBody] IContactService contactService)
        {
            _contactService = contactService;
        }

        [HttpGet("get")]
        public IActionResult GetAll()
        {
            return Ok(_contactService.GetAll());
        }

        [HttpPost("sendFeedback")]
        public IActionResult Add([FromBody] ContactDtoForInsert record)
        {
            _contactService.Insert(record);
            return Ok();
        }
    }
}