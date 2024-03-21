using Moq;
using FluentAssertions;
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
    public class ArticleControllerTests_Moq
    {
        private ArticleService _articleService;
        private ArticleController _articleController;

        private Mock<IRepository<Article>> _articleRepositoryMock;
        private Mock<IRepository<User>> _userRepositoryMock;
        private Mock<IRepository<ArticleImage>> _articleImageRepositoryMock;
        private Mock<IRepository<Category>> _categoryImageRepositoryMock;
        private Mock<IRepository<Comment>> _commentRepositoryMock;
        private Mock<IRepository<Feedback>> _feedbackRepositoryMock;
        private Mock<IRepository<FavoriteArticle>> _favoriteRepositoryMock;

        public ArticleControllerTests_Moq()
        {
            _articleRepositoryMock = new Mock<IRepository<Article>>();
            _userRepositoryMock = new Mock<IRepository<User>>();
            _articleImageRepositoryMock = new Mock<IRepository<ArticleImage>>();
            _categoryImageRepositoryMock   = new Mock<IRepository<Category>>();
            _commentRepositoryMock = new Mock<IRepository<Comment>>();
            _feedbackRepositoryMock = new Mock<IRepository<Feedback>>();
            _favoriteRepositoryMock = new Mock<IRepository<FavoriteArticle>>();

            _articleService = new ArticleService(_articleRepositoryMock.Object,
                _userRepositoryMock.Object,
                _articleImageRepositoryMock.Object,
                _categoryImageRepositoryMock.Object,
                _commentRepositoryMock.Object,
                _feedbackRepositoryMock.Object,
                _favoriteRepositoryMock.Object);

            _articleController = new ArticleController(_articleService);
        }

        [Fact]
        public void Given_One_User_When_Deleting_User_Should_Delete_User_Successfully()
        {
            // Arrange
            var articleToDelete = new Article();
            _articleRepositoryMock.Setup(repo => repo.Insert(new Article()));
            _articleRepositoryMock.Setup(repo => repo.Get(1)).Returns(articleToDelete);

            // HttpContext și HttpRequest
            var httpContextMock = new Mock<HttpContext>();
            var httpRequestMock = new Mock<HttpRequest>();

            var token = new ManagementToken().GetToken(0);
            httpRequestMock.Setup(req => req.Headers["Authorization"]).Returns(token);

            // Asocire HttpRequest la HttpContext
            httpContextMock.Setup(ctx => ctx.Request).Returns(httpRequestMock.Object);

            // Asociere HttpContext la controller
            _articleController.ControllerContext = new ControllerContext
            {
                HttpContext = httpContextMock.Object
            };

            // Act
            var result = _articleController.Delete(1);

            // Assert
            result.Should().BeOfType<OkResult>();
        }

        [Fact]
        public void Given_NewArticle_ValidData_When_InsertArticle_Then_Successfully()
        {
            // Arrange
            var article = new CreateArticleDto( "Article1",DateTime.Now, 
                "The New York Times seeks the truth and helps people understand the world. With 1700 journalists reporting from more than 150 countries, we provide live",
                "Iasi",
                DateTime.Now.AddMonths(1),
                "facebook.com",
                "twitter.com",
                "instagram.com",
                1,
                new List<string>(),
                new List<ArticleImageDto>());

            // HttpContext și HttpRequest
            var httpContextMock = new Mock<HttpContext>();
            var httpRequestMock = new Mock<HttpRequest>();

            var token = new ManagementToken().GetToken(0);
            httpRequestMock.Setup(req => req.Headers["Authorization"]).Returns(token);

            // Asocire HttpRequest la HttpContext
            httpContextMock.Setup(ctx => ctx.Request).Returns(httpRequestMock.Object);

            // Asociere HttpContext la controller
            _articleController.ControllerContext = new ControllerContext
            {
                HttpContext = httpContextMock.Object
            };

            // Act
            var result = _articleController.Add(article);

            // Assert
            result.Should().BeOfType<OkResult>();
        }

        [Fact]
        public void Given_ArticleExists_When_UpdatingExistingArticle_Then_ArticleShouldBeUpdated()
        {
            // Arrange
            var articleNew = new CreateArticleDto("Title",DateTime.Now,"Content...","Iasi",null,"","","",1,new List<string>(),new List<ArticleImageDto>());
            var articleOld = new Article("TitleNew",DateTime.Now,0,"ContentNew...","Iasi",null,"","","",new List<Category>(),new List<ArticleImage>());

            _articleRepositoryMock.Setup(repo => repo.Get(1)).Returns(articleOld);

            // HttpContext și HttpRequest
            var httpContextMock = new Mock<HttpContext>();
            var httpRequestMock = new Mock<HttpRequest>();

            var token = new ManagementToken().GetToken(0);
            httpRequestMock.Setup(req => req.Headers["Authorization"]).Returns(token);

            // Asocire HttpRequest la HttpContext
            httpContextMock.Setup(ctx => ctx.Request).Returns(httpRequestMock.Object);

            // Asociere HttpContext la controller
            _articleController.ControllerContext = new ControllerContext
            {
                HttpContext = httpContextMock.Object
            };

            // Act
            var result = _articleController.Update(1, articleNew);

            // Assert
            result.Should().BeOfType<OkResult>();
        }

        [Fact]
        public void Given_ArticleDoesNotExist_When_UpdatingArticle_Then_NothingShouldHappen()
        {
            // Arrange
            var articleNew = new CreateArticleDto("Title", DateTime.Now, "Content...", "Iasi", null, "", "", "", 1, new List<string>(), new List<ArticleImageDto>());
            var articleOld = new Article("TitleNew", DateTime.Now, 1, "ContentNew...", "Iasi", null, "", "", "", new List<Category>(), new List<ArticleImage>());

            _articleRepositoryMock.Setup(repo => repo.Get(1)).Returns(articleOld);

            // HttpContext și HttpRequest
            var httpContextMock = new Mock<HttpContext>();
            var httpRequestMock = new Mock<HttpRequest>();

            var token = new ManagementToken().GetToken(0);
            httpRequestMock.Setup(req => req.Headers["Authorization"]).Returns(token);

            // Asocire HttpRequest la HttpContext
            httpContextMock.Setup(ctx => ctx.Request).Returns(httpRequestMock.Object);

            // Asociere HttpContext la controller
            _articleController.ControllerContext = new ControllerContext
            {
                HttpContext = httpContextMock.Object
            };

            // Act
            var result = _articleController.Update(2, articleNew);

            // Assert
            result.Should().BeOfType<NotFoundResult>();
        }

        [Fact]
        public void Given_One_User_When_Deleting_User_Should_UnauthorizedAccessError()
        {
            // Arrange
            var articleToDelete = new Article();
            _articleRepositoryMock.Setup(repo => repo.Insert(new Article()));
            _articleRepositoryMock.Setup(repo => repo.Get(1)).Returns(articleToDelete);
            // HttpContext și HttpRequest
            var httpContextMock = new Mock<HttpContext>();
            var httpRequestMock = new Mock<HttpRequest>();

            httpRequestMock.Setup(req => req.Headers["Authorization"]).Returns("");

            // Asocire HttpRequest la HttpContext
            httpContextMock.Setup(ctx => ctx.Request).Returns(httpRequestMock.Object);

            // Asociere HttpContext la controller
            _articleController.ControllerContext = new ControllerContext
            {
                HttpContext = httpContextMock.Object
            };

            // Act
            var result = _articleController.Delete(1);

            // Assert
            result.Should().BeOfType<UnauthorizedResult>();
        }

        [Fact]
        public void Given_NewArticle_ValidData_When_InsertArticle_Then_UnauthorizedAccessError()
        {
            // Arrange
            var article = new CreateArticleDto("Article1", DateTime.Now,
                "The New York Times seeks the truth and helps people understand the world. With 1700 journalists reporting from more than 150 countries, we provide live",
                "Iasi",
                DateTime.Now.AddMonths(1),
                "facebook.com",
                "twitter.com",
                "instagram.com",
                1,
                new List<string>(),
                new List<ArticleImageDto>());

            // HttpContext și HttpRequest
            var httpContextMock = new Mock<HttpContext>();
            var httpRequestMock = new Mock<HttpRequest>();

            httpRequestMock.Setup(req => req.Headers["Authorization"]).Returns("");

            // Asocire HttpRequest la HttpContext
            httpContextMock.Setup(ctx => ctx.Request).Returns(httpRequestMock.Object);

            // Asociere HttpContext la controller
            _articleController.ControllerContext = new ControllerContext
            {
                HttpContext = httpContextMock.Object
            };

            //Act
             var result =    _articleController.Add(article);

            //Assert
            result.Should().BeOfType<UnauthorizedResult>();
        }

        [Fact]
        public void Given_ArticleExists_When_UpdatingExistingArticle_Then_UnauthorizedAccessError()
        {
            // Arrange
            var articleNew = new CreateArticleDto("Title", DateTime.Now, "Content...", "Iasi", null, "", "", "", 1, new List<string>(), new List<ArticleImageDto>());
            var articleOld = new Article("TitleNew", DateTime.Now, 0, "ContentNew...", "Iasi", null, "", "", "", new List<Category>(), new List<ArticleImage>());

            _articleRepositoryMock.Setup(repo => repo.Get(1)).Returns(articleOld);

            // HttpContext și HttpRequest
            var httpContextMock = new Mock<HttpContext>();
            var httpRequestMock = new Mock<HttpRequest>();

            httpRequestMock.Setup(req => req.Headers["Authorization"]).Returns("");

            // Asocire HttpRequest la HttpContext
            httpContextMock.Setup(ctx => ctx.Request).Returns(httpRequestMock.Object);

            // Asociere HttpContext la controller
            _articleController.ControllerContext = new ControllerContext
            {
                HttpContext = httpContextMock.Object
            };

            // Act
            var result = _articleController.Update(1, articleNew);

            // Assert
            result.Should().BeOfType<UnauthorizedResult>();
        }

        [Fact]
        public void Given_ArticleDoesNotExist_When_UpdatingArticle_Then_UnauthorizedAccessError()
        {
            // Arrange
            var articleNew = new CreateArticleDto("Title", DateTime.Now, "Content...", "Iasi", null, "", "", "", 1, new List<string>(), new List<ArticleImageDto>());
            var articleOld = new Article("TitleNew", DateTime.Now, 1, "ContentNew...", "Iasi", null, "", "", "", new List<Category>(), new List<ArticleImage>());

            _articleRepositoryMock.Setup(repo => repo.Get(1)).Returns(articleOld);

            // HttpContext și HttpRequest
            var httpContextMock = new Mock<HttpContext>();
            var httpRequestMock = new Mock<HttpRequest>();

            httpRequestMock.Setup(req => req.Headers["Authorization"]).Returns("");

            // Asocire HttpRequest la HttpContext
            httpContextMock.Setup(ctx => ctx.Request).Returns(httpRequestMock.Object);

            // Asociere HttpContext la controller
            _articleController.ControllerContext = new ControllerContext
            {
                HttpContext = httpContextMock.Object
            };

            // Act
            var result = _articleController.Update(2, articleNew);

            // Assert
            result.Should().BeOfType<UnauthorizedResult>();
        }
    }
}