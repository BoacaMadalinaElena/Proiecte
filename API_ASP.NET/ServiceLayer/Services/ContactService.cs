using DomainLayer.Models;
using RepositoryLayer;
using ServiceLayer.Contracts;
using ServiceLayer.DtoModels;
using ServiceLayer.Notify;
using System.Text.RegularExpressions;

namespace ServiceLayer.Services
{
    public class ContactService : IContactService
    {
        private readonly IRepository<Contact> _contactRepository;
        private readonly IEmail _email;

        public ContactService(IRepository<Contact> contactRepository,IEmail email)
        {
            _contactRepository = contactRepository;
            _email = email;
        }

        public IEnumerable<ContactDto> GetAll()
        {
            return _contactRepository.GetAll().Select(u => new ContactDto(u.Name, u.Email, u.Subject, u.Message, u.DateOfSend));
        }

        public void Insert(ContactDtoForInsert entity)
        {
            // Patern pt email
            string pattern = @"\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}\b";

            var isValid = Regex.IsMatch(entity.Email, pattern);
            if (isValid == false)
            {
                throw new InvalidOperationException();
            }

            var record = new Contact(entity.Name, entity.Email, entity.Subject, entity.Message, DateTime.Now);
            _email.SendFeedback(new ContactDto(entity.Name, entity.Email, entity.Subject, entity.Message, DateTime.Now));
            _contactRepository.Insert(record);
        }
    }
}
