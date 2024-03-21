using Moq;
using FluentAssertions;
using ServiceLayer;
using RepositoryLayer;
using ServiceLayer.Services;
using DomainLayer.Models;
using ServiceLayer.DtoModels;
using PresentationLayer.Controllers;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Http;
using ServiceLayer.SessionVariable;
using Microsoft.IdentityModel.Tokens;

namespace ArticleTest.UnitTests
{
    public class UserControllerTests_Moq
    {
        private UserController _userController;
        private UserService _userService;
        private Mock<IRepository<User>> _userRepositoryMock;
        private Mock<IRepository<Article>> _articleRepositoryMock;
        private Mock<IRepository<FavoriteArticle>> _favoriteRepositoryMock;

        public UserControllerTests_Moq()
        {
            _userRepositoryMock = new Mock<IRepository<User>>();
            _articleRepositoryMock = new Mock<IRepository<Article>>();
            _favoriteRepositoryMock = new Mock<IRepository<FavoriteArticle>>();

            _userService = new UserService(_userRepositoryMock.Object, _favoriteRepositoryMock.Object, _articleRepositoryMock.Object);

            _userController = new UserController(_userService);
        }

        [Fact]
        public void Given_NoExistingUserCreator_When_AddingNewUser_Then_UserSuccessfullyAdded()
        {
            // Arrange
            var existingUsers = new List<User>() { new User("John", "john@gmail.com", "Pass1234", true, "Description...", "", "") };
            _userRepositoryMock.Setup(repo => repo.GetAll()).Returns(existingUsers);

            var newUser = new UserContentCreatorDto(1, "Ion", "ion@gmail.com", "Pass1234", true, "Description...", "", "");

            // Act
            var result = _userController.AddCreator(newUser);

            // Assert
            result.Should().BeOfType<BadRequestResult>();
        }

        [Fact]
        public void Given_NoExistingUser_When_AddingNewUser_Then_BadRequest()
        {
            // Arrange
            var existingUsers = new List<User>() { new User("John", "john@gmail.com", "Pass1234", true, "Description...", "", "") };
            _userRepositoryMock.Setup(repo => repo.GetAll()).Returns(existingUsers);

            var newUser = new UserDtoCreateAcount( "Ion", "ion@gmail.com", "Pass1234");

            // Act
            var result = _userController.Add(newUser);

            // Assert
            result.Should().BeOfType<BadRequestResult>();
        }

        [Fact]
        public void Given_NoExistingUser_When_AddingNewUser_Then_UserSuccessfullyAdded()
        {
            // Arrange
            var existingUsers = new List<User>() { new User("John", "john@gmail.com", "Pass1234", true, "Description...", "", "") };
            _userRepositoryMock.Setup(repo => repo.GetAll()).Returns(existingUsers);

            var newUser = new UserDtoCreateAcount("Ion", "ion@gmail.com", "Pass1234");

            // Act
            var result = _userController.Add(newUser);

            // Assert
            result.Should().BeOfType<BadRequestResult>();
        }

        [Fact]
        public void Given_UserHasNoFavorites_When_AddingItemToFavorites_Then_ItemShouldBeInFavoritesList()
        {
            // Arrange
            var user = new User("Name", "email@gmail.com", "passwordA123", true, "Description...", "link.com", "");
            _userRepositoryMock.Setup(repo => repo.Get(1)).Returns(user);

            var article = new Article("Title", DateTime.Now, 1, "Content...", "Iasi", DateTime.Now.AddMonths(2), "", "", "", new List<Category>(), new List<ArticleImage>());
            _articleRepositoryMock.Setup(repo => repo.Get(1)).Returns(article);

            // HttpContext și HttpRequest
            var httpContextMock = new Mock<HttpContext>();
            var httpRequestMock = new Mock<HttpRequest>();

            var token = new ManagementToken().GetToken(1);
            httpRequestMock.Setup(req => req.Headers["Authorization"]).Returns(token);

            // Asocire HttpRequest la HttpContext
            httpContextMock.Setup(ctx => ctx.Request).Returns(httpRequestMock.Object);

            // Asociere HttpContext la controller
            _userController.ControllerContext = new ControllerContext
            {
                HttpContext = httpContextMock.Object
            };

            // Act
            var result = _userController.AddToFavorites(1);

            // Assert 
            result.Should().BeOfType<OkResult>();
        }

        [Fact]
        public void Given_UserIsNotAuthenticated_When_AddingItemToFavorites_Then_ItemShouldNotBeAdded()
        {
            // Arrange
            var user = new User("Name", "email@gmail.com", "passwordA123", true, "Description...", "link.com", "");
            _userRepositoryMock.Setup(repo => repo.Get(1)).Returns(user);

            var article = new Article("Title", DateTime.Now, 1, "Content...", "Iasi", DateTime.Now.AddMonths(2), "", "", "", new List<Category>(), new List<ArticleImage>());
            _articleRepositoryMock.Setup(repo => repo.Get(1)).Returns(article);

            // HttpContext și HttpRequest
            var httpContextMock = new Mock<HttpContext>();
            var httpRequestMock = new Mock<HttpRequest>();

            httpRequestMock.Setup(req => req.Headers["Authorization"]).Returns("");

            // Asocire HttpRequest la HttpContext
            httpContextMock.Setup(ctx => ctx.Request).Returns(httpRequestMock.Object);

            // Asociere HttpContext la controller
            _userController.ControllerContext = new ControllerContext
            {
                HttpContext = httpContextMock.Object
            };

            // Act
            var result = _userController.AddToFavorites(1);

            // Assert 
            result.Should().BeOfType<UnauthorizedResult>();
        }

        [Fact]
        public void RemoveItemFromFavorites_When_UserHasFavorites_Then_ItemShouldNotBeInFavoritesList()
        {
            // Arrange
            var user = new User("Name", "email@gmail.com", "passwordA123", true, "Description...", "link.com", "");
            _userRepositoryMock.Setup(repo => repo.Get(1)).Returns(user);

            var article = new Article("Title", DateTime.Now, 1, "Content...", "Iasi", DateTime.Now.AddMonths(2), "", "", "", new List<Category>(), new List<ArticleImage>());
            _articleRepositoryMock.Setup(repo => repo.Get(1)).Returns(article);

            // HttpContext și HttpRequest
            var httpContextMock = new Mock<HttpContext>();
            var httpRequestMock = new Mock<HttpRequest>();

            var token = new ManagementToken().GetToken(1);
            httpRequestMock.Setup(req => req.Headers["Authorization"]).Returns(token);

            // Asocire HttpRequest la HttpContext
            httpContextMock.Setup(ctx => ctx.Request).Returns(httpRequestMock.Object);

            // Asociere HttpContext la controller
            _userController.ControllerContext = new ControllerContext
            {
                HttpContext = httpContextMock.Object
            };

            // Act
            var result = _userController.RemoveToFavorites(1);

            // Assert 
            result.Should().BeOfType<OkResult>();
        }

        [Fact]
        public void Given_UserIsNotAuthenticated_When_RemovingItemToFavorites_Then_ItemShouldNotBeInFavoritesList()
        {
            // Arrange
            var user = new User("Name", "email@gmail.com", "passwordA123", true, "Description...", "link.com", "");
            _userRepositoryMock.Setup(repo => repo.Get(1)).Returns(user);

            var article = new Article("Title", DateTime.Now, 1, "Content...", "Iasi", DateTime.Now.AddMonths(2), "", "", "", new List<Category>(), new List<ArticleImage>());
            _articleRepositoryMock.Setup(repo => repo.Get(1)).Returns(article);

            // HttpContext și HttpRequest
            var httpContextMock = new Mock<HttpContext>();
            var httpRequestMock = new Mock<HttpRequest>();

            httpRequestMock.Setup(req => req.Headers["Authorization"]).Returns("");

            // Asocire HttpRequest la HttpContext
            httpContextMock.Setup(ctx => ctx.Request).Returns(httpRequestMock.Object);

            // Asociere HttpContext la controller
            _userController.ControllerContext = new ControllerContext
            {
                HttpContext = httpContextMock.Object
            };

            // Act
            var result = _userController.RemoveToFavorites(1);

            // Assert 
            result.Should().BeOfType<UnauthorizedResult>();
        }

        [Fact]
        public void Given_ValidCredentials_When_UserAttemptsLogin_Then_LoginSuccessful()
        {
            // Arrange am nevoie de parola criptate
            // Metoda de criptare e sigur ok
            var user1 = new User("Ion Popescu1", "ionpopescu1@gmail.com", _userService.HashPassword("password1"), false, "string", "string", "string");
            var user2 = new User("Ion Popescu2", "ionpopescu2@gmail.com", _userService.HashPassword("password2"), false, "string", "string", "string");
            var users = new List<User>();
            var userLogin = new UserLoginDto("ionpopescu1@gmail.com", "password1");
            users.Add(user2);
            users.Add(user1);

            _userRepositoryMock.Setup(repo => repo.Insert(user1));
            _userRepositoryMock.Setup(repo => repo.Insert(user2));
            _userRepositoryMock.Setup(repo => repo.GetAll()).Returns(users);
            _userRepositoryMock.Setup(repo => repo.Get(0)).Returns(user1);

            // HttpContext și HttpRequest
            var httpContextMock = new Mock<HttpContext>();
            var httpRequestMock = new Mock<HttpRequest>();

            var token = new ManagementToken().GetToken(1);
            httpRequestMock.Setup(req => req.Headers["Authorization"]).Returns(token);

            // Asocire HttpRequest la HttpContext
            httpContextMock.Setup(ctx => ctx.Request).Returns(httpRequestMock.Object);

            // Asociere HttpContext la controller
            _userController.ControllerContext = new ControllerContext
            {
                HttpContext = httpContextMock.Object
            };
            // Act
            var result = _userController.Login(userLogin);

            // Assert daca e null nu sa facut loginul
            result.Should().BeOfType<OkObjectResult>();
        }

        [Fact]
        public void Given_One_User_When_Updating_Password_Should_Update_Password_Successfully()
        {
            // Arrange
            var user = new User("Ion Popescu", "ionpopescu@gmail.com", "password", false, "string", "string", "string");
            var newPass = "newPass";

            _userRepositoryMock.Setup(repo => repo.Get(1)).Returns(user);

            // HttpContext și HttpRequest
            var httpContextMock = new Mock<HttpContext>();
            var httpRequestMock = new Mock<HttpRequest>();

            var token = new ManagementToken().GetToken(1);
            httpRequestMock.Setup(req => req.Headers["Authorization"]).Returns(token);

            // Asocire HttpRequest la HttpContext
            httpContextMock.Setup(ctx => ctx.Request).Returns(httpRequestMock.Object);

            // Asociere HttpContext la controller
            _userController.ControllerContext = new ControllerContext
            {
                HttpContext = httpContextMock.Object
            };

            // Act
            var result = _userController.UpdatePassword(new NewPasswordDto("password2"));

            // Assert

            result.Should().BeOfType<OkResult>();
        }

        [Fact]
        public void Given_Unauthorized_User_When_Updating_Password_Should_Deny_Access_To_Update_Password()
        {
            // Arrange
            var user = new User("Ion Popescu", "ionpopescu@gmail.com", "password", false, "string", "string", "string");
            var newPass = "newPass";

            _userRepositoryMock.Setup(repo => repo.Get(1)).Returns(user);

            // HttpContext și HttpRequest
            var httpContextMock = new Mock<HttpContext>();
            var httpRequestMock = new Mock<HttpRequest>();

            httpRequestMock.Setup(req => req.Headers["Authorization"]).Returns("" );

            // Asocire HttpRequest la HttpContext
            httpContextMock.Setup(ctx => ctx.Request).Returns(httpRequestMock.Object);

            // Asociere HttpContext la controller
            _userController.ControllerContext = new ControllerContext
            {
                HttpContext = httpContextMock.Object
            };

            // Act
            var result = _userController.UpdatePassword(new NewPasswordDto("password2"));

            // Assert

            result.Should().BeOfType<UnauthorizedResult>();
        }

        [Fact]
        public void Given_Unauthorized_2_User_When_Updating_Password_Should_Deny_Access_To_Update_Password()
        {
            // Arrange
            var user = new User("Ion Popescu", "ionpopescu@gmail.com", "password", false, "string", "string", "string");
            var newPass = "newPass";

            _userRepositoryMock.Setup(repo => repo.Get(1)).Returns(user);

            // HttpContext și HttpRequest
            var httpContextMock = new Mock<HttpContext>();
            var httpRequestMock = new Mock<HttpRequest>();
            var token = new ManagementToken().GetToken(1);
            httpRequestMock.Setup(req => req.Headers["Authorization"]).Returns(token + "a");

            // Asocire HttpRequest la HttpContext
            httpContextMock.Setup(ctx => ctx.Request).Returns(httpRequestMock.Object);

            // Asociere HttpContext la controller
            _userController.ControllerContext = new ControllerContext
            {
                HttpContext = httpContextMock.Object
            };

            // Act & assert
            Assert.Throws<SecurityTokenSignatureKeyNotFoundException>(() =>
            {
                // verificare token care arunca exceptie SecurityTokenSignatureKeyNotFoundException
                _userController.UpdatePassword(new NewPasswordDto("password2"));
            });
        }
    }
}