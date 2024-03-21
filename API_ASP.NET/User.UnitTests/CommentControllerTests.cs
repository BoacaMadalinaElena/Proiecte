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

namespace ArticleTest.UnitTests
{
    public class CommentControllerTests_Moq
    {
        private CommentController _commentController;
        private CommentService _commentService;
        private UserService _userService;

        private Mock<IRepository<User>> _userRepositoryMock;
        private Mock<IRepository<Comment>> _commentRepositoryMock;
        private Mock<IRepository<FavoriteArticle>> _favoriteArticleMock;
        private Mock<IRepository<Article>> _articleRepositoryMock;

        public CommentControllerTests_Moq()
        {

            _userRepositoryMock = new Mock<IRepository<User>>();
            _commentRepositoryMock = new Mock<IRepository<Comment>>();
            _favoriteArticleMock = new Mock<IRepository<FavoriteArticle>>();
            _articleRepositoryMock = new Mock<IRepository<Article>>();  

            _commentService = new CommentService(_commentRepositoryMock.Object,_userRepositoryMock.Object);
            _userService = new UserService(_userRepositoryMock.Object, _favoriteArticleMock.Object, _articleRepositoryMock.Object);

            _commentController = new CommentController(_commentService,_userService);
        }

        [Fact]
        public void DeleteExistingComment_SuccessfullyDeletedComment()
        {
            // Arrange
            var commentDto = new Comment(0, 1, "Un nou comentariu", DateTime.Now);
            _commentRepositoryMock.Setup(repo => repo.Insert(commentDto));
            _commentRepositoryMock.Setup(repo => repo.Get(1)).Returns(commentDto);
            _commentRepositoryMock.Setup(repo => repo.GetAll()).Returns(new List<Comment>() { commentDto });
            var user = new User("ion", "ion@gmail.com", "Ion.1234", true, "Description..", "", "");

            _userRepositoryMock.Setup(repo => repo.Get(0)).Returns(user);
            _userRepositoryMock.Setup(repo => repo.GetAll()).Returns(new List<User>() { user });    
            
            // HttpContext și HttpRequest
            var httpContextMock = new Mock<HttpContext>();
            var httpRequestMock = new Mock<HttpRequest>();

            var token = new ManagementToken().GetToken(0);
            httpRequestMock.Setup(req => req.Headers["Authorization"]).Returns(token);

            // Asocire HttpRequest la HttpContext
            httpContextMock.Setup(ctx => ctx.Request).Returns(httpRequestMock.Object);

            // Asociere HttpContext la controller
            _commentController.ControllerContext = new ControllerContext
            {
                HttpContext = httpContextMock.Object
            };

            // Act
            var result = _commentController.Delete(1);

            // Assert 
            result.Should().BeOfType<OkResult>();
        }

        [Fact]
        public void UnauthorizedDeleteComment_AttemptToDeleteComment_Unsuccessful()
        {
            // Arrange
            var commentDto = new Comment(0, 1, "Un nou comentariu", DateTime.Now);
            _commentRepositoryMock.Setup(repo => repo.Insert(commentDto));
            _commentRepositoryMock.Setup(repo => repo.Get(1)).Returns(commentDto);
            _commentRepositoryMock.Setup(repo => repo.GetAll()).Returns(new List<Comment>() { commentDto });
            var user = new User("ion", "ion@gmail.com", "Ion.1234", true, "Description..", "", "");

            _userRepositoryMock.Setup(repo => repo.Get(0)).Returns(user);
            _userRepositoryMock.Setup(repo => repo.GetAll()).Returns(new List<User>() { user });
            
            // HttpContext și HttpRequest
            var httpContextMock = new Mock<HttpContext>();
            var httpRequestMock = new Mock<HttpRequest>();

            httpRequestMock.Setup(req => req.Headers["Authorization"]).Returns("");

            // Asocire HttpRequest la HttpContext
            httpContextMock.Setup(ctx => ctx.Request).Returns(httpRequestMock.Object);

            // Asociere HttpContext la controller
            _commentController.ControllerContext = new ControllerContext
            {
                HttpContext = httpContextMock.Object
            };

            // Act
            var result = _commentController.Delete(1);

            // Assert 
            result.Should().BeOfType<UnauthorizedResult>();
        }

        [Fact]
        public void Given_NewComment_When_InsertComment_Then_Successfully()
        {
            // Arrange
            // Se pregateste comment-ul valid de test
            var feed = new CreateCommentDto(1, 0, 1, "Un nou comentariu", DateTime.Now);

            // HttpContext și HttpRequest
            var httpContextMock = new Mock<HttpContext>();
            var httpRequestMock = new Mock<HttpRequest>();

            var token = new ManagementToken().GetToken(0);
            httpRequestMock.Setup(req => req.Headers["Authorization"]).Returns(token);

            // Asocire HttpRequest la HttpContext
            httpContextMock.Setup(ctx => ctx.Request).Returns(httpRequestMock.Object);

            // Asociere HttpContext la controller
            _commentController.ControllerContext = new ControllerContext
            {
                HttpContext = httpContextMock.Object
            };

            // Act
            // Apelez metoda de inserare
            var result = _commentController.Insert(feed);

            // Assert
            result.Should().BeOfType<OkResult>();
        }

        [Fact]
        public void Given_NewComment_When_InsertComment_Then_Unauthorized()
        {
            // Arrange
            // Se pregateste comment-ul valid de test
            var feed = new CreateCommentDto(1, 0, 1, "Un nou comentariu", DateTime.Now);

            // HttpContext și HttpRequest
            var httpContextMock = new Mock<HttpContext>();
            var httpRequestMock = new Mock<HttpRequest>();

            httpRequestMock.Setup(req => req.Headers["Authorization"]).Returns("");

            // Asocire HttpRequest la HttpContext
            httpContextMock.Setup(ctx => ctx.Request).Returns(httpRequestMock.Object);

            // Asociere HttpContext la controller
            _commentController.ControllerContext = new ControllerContext
            {
                HttpContext = httpContextMock.Object
            };

            // Act
            // Apelez metoda de inserare
            var result = _commentController.Insert(feed);

            // Assert
            result.Should().BeOfType<UnauthorizedResult>();
        }

        [Fact]
        public void Given_CommentExists_When_UpdatingExistingComment_Then_CommentShouldBeUpdated()
        {
            // Arrange
            // Se pregateste comment-ul valid de test
            var commNew = new CreateCommentDto(1, 0, 1, "Un nou comentariu", DateTime.Now);
            var commOld = new Comment(0, 1, "Content", DateTime.Now.AddDays(-1));

            _commentRepositoryMock.Setup(repo => repo.Get(1)).Returns(commOld);

            // HttpContext și HttpRequest
            var httpContextMock = new Mock<HttpContext>();
            var httpRequestMock = new Mock<HttpRequest>();

            var token = new ManagementToken().GetToken(0);
            httpRequestMock.Setup(req => req.Headers["Authorization"]).Returns(token);

            // Asocire HttpRequest la HttpContext
            httpContextMock.Setup(ctx => ctx.Request).Returns(httpRequestMock.Object);

            // Asociere HttpContext la controller
            _commentController.ControllerContext = new ControllerContext
            {
                HttpContext = httpContextMock.Object
            };

            // Act
            // Apelez metoda de inserare
            var result = _commentController.Update(1, commNew);

            // Assert
            // Verific ca metoda a fost apelata de salvare intrucat nu se apeleaza update din nivelul inferior
            result.Should().BeOfType<OkResult>();
        }

        [Fact]
        public void Given_CommentExists_When_UpdatingExistingComment_Then_UnauthorizedToUpdateComment()
        {
            // Arrange
            // Se pregateste comment-ul valid de test
            var commNew = new CreateCommentDto(1, 0, 1, "Un nou comentariu", DateTime.Now);
            var commOld = new Comment(0, 1, "Content", DateTime.Now.AddDays(-1));

            _commentRepositoryMock.Setup(repo => repo.Get(1)).Returns(commOld);

            // HttpContext și HttpRequest
            var httpContextMock = new Mock<HttpContext>();
            var httpRequestMock = new Mock<HttpRequest>();

            httpRequestMock.Setup(req => req.Headers["Authorization"]).Returns("");

            // Asocire HttpRequest la HttpContext
            httpContextMock.Setup(ctx => ctx.Request).Returns(httpRequestMock.Object);

            // Asociere HttpContext la controller
            _commentController.ControllerContext = new ControllerContext
            {
                HttpContext = httpContextMock.Object
            };

            // Act
            // Apelez metoda de inserare
            var result = _commentController.Update(1, commNew);

            // Assert
            // Verific ca metoda a fost apelata de salvare intrucat nu se apeleaza update din nivelul inferior
            result.Should().BeOfType<UnauthorizedResult>();
        }
    }
}
