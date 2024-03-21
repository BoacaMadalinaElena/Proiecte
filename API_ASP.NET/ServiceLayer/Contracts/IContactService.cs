using DomainLayer.Models;
using ServiceLayer.DtoModels;

namespace ServiceLayer.Contracts
{
    public interface IContactService
    {
        IEnumerable<ContactDto> GetAll();

        void Insert(ContactDtoForInsert entity);
    }
}
