using Moq;
using FluentAssertions;
using ServiceLayer;
using RepositoryLayer;
using ServiceLayer.Services;
using DomainLayer.Models;
using ServiceLayer.DtoModels;
using ServiceLayer.Notify;
using PresentationLayer.Controllers;
using ServiceLayer.Contracts;
using Microsoft.AspNetCore.Mvc;

namespace ContactTest.UnitTests
{
    public class ContactControllerTests_Moq
    {
        private ContactController _contactController;

        private IContactService _contactService;
        private Mock<IRepository<Contact>> _contactRepositoryMock;
        private Mock<IEmail> _emailMock;

        public ContactControllerTests_Moq()
        {
            _contactRepositoryMock = new Mock<IRepository<Contact>>();
            // Nu se trimite email
            _emailMock = new Mock<IEmail>();

            _contactService = new ContactService(_contactRepositoryMock.Object, _emailMock.Object);


            _contactController = new ContactController(_contactService );
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
            var result = _contactController.Add(contact);

            // Assert
            result.Should().BeOfType<OkResult>();
        }



    }
}