using Moq;
using FluentAssertions;
using ServiceLayer;
using RepositoryLayer;
using ServiceLayer.Services;
using DomainLayer.Models;
using ServiceLayer.DtoModels;
using Microsoft.IdentityModel.Tokens;
using Newtonsoft.Json.Linq;
using ServiceLayer.SessionVariable;
using ServiceLayer.Notify;

namespace ContactTest.UnitTests
{
    public class ContactServiceTests_Moq
    {
        private ContactService _systemUnderTest;

        private Mock<IRepository<Contact>> _contactRepositoryMock;
        private Mock<IEmail> _emailMock;
        public ContactServiceTests_Moq()
        {
            _contactRepositoryMock = new Mock<IRepository<Contact>>();
            // Nu se trimite email
            _emailMock = new Mock<IEmail>();

            _systemUnderTest = new ContactService( _contactRepositoryMock.Object,_emailMock.Object);
        }

        [Fact]
        public void Given_NewContact_When_InsertContact_Then_Successfully()
        {
            // Arrange
            var contact = new ContactDtoForInsert("Name", "name@gmail.com", "Subject...", "Message....");

            // Creează obiectul Mock pentru serviciul de email
            _emailMock.Setup(x => x.SendFeedback(It.IsAny<ContactDto>()));

            // Act
            // Apelez metoda de inserare
            _systemUnderTest.Insert(contact);

            // Assert
            // Verific ca metoda _contactRepository.Insert a fost apelată cu parametrul corect
            _contactRepositoryMock.Verify(x => x.Insert(It.Is<Contact>(u => (u.Name == contact.Name))), Times.Once);

            // Verific ca metoda _email.SendFeedback nu a fost apelata
            _emailMock.Verify(x => x.SendFeedback(It.IsAny<ContactDto>()), Times.Once);
        }



    }
}