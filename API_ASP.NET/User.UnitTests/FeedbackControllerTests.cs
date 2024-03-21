using Moq;
using FluentAssertions;
using RepositoryLayer;
using ServiceLayer.Services;
using DomainLayer.Models;
using ServiceLayer.DtoModels;
using ServiceLayer.SessionVariable;
using PresentationLayer.Controllers;
using ServiceLayer.Contracts;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Http;

namespace FeedbackTest.UnitTests
{
    public class FeedbackControllerTests_Moq
    {
        private FeedbackController _feedbackController;

        private Mock<IRepository<Feedback>> _feedbackRepositoryMock;
        private IFeedbackService _feedbackService;

        public FeedbackControllerTests_Moq()
        {
            _feedbackRepositoryMock = new Mock<IRepository<Feedback>>();

            _feedbackService = new FeedbackService(_feedbackRepositoryMock.Object);

            _feedbackController = new FeedbackController(_feedbackService);
        }

    
        [Fact]
        public void DeleteExistingFeedback_SuccessfullyDeletedFeedback()
        {
            // Arrange
            var feedbackDto = new Feedback(1, 0, true);
            _feedbackRepositoryMock.Setup(repo => repo.Insert(feedbackDto));
            _feedbackRepositoryMock.Setup(repo => repo.GetAll()).Returns(new[] { feedbackDto });

            // HttpContext și HttpRequest
            var httpContextMock = new Mock<HttpContext>();
            var httpRequestMock = new Mock<HttpRequest>();

            var token = new ManagementToken().GetToken(0);
            httpRequestMock.Setup(req => req.Headers["Authorization"]).Returns(token);

            // Asocire HttpRequest la HttpContext
            httpContextMock.Setup(ctx => ctx.Request).Returns(httpRequestMock.Object);

            // Asociere HttpContext la controller
            _feedbackController.ControllerContext = new ControllerContext
            {
                HttpContext = httpContextMock.Object
            };

            _feedbackRepositoryMock.Setup(repo => repo.Get(1)).Returns(feedbackDto);

            // Act
            // Am doar un feedback adaugat
            var result = _feedbackController.Delete(new FeedbackDto(1, 0, false));

            // Assert
            result.Should().BeOfType<OkResult>();
        }

        [Fact]
        public void Given_NewFeedback_When_InsertFeedback_Then_Successfully()
        {
            // Arrange
            // Se pregateste feeback-ul valid de test
            var feed = new FeedbackDto(1,1,true);

            // HttpContext și HttpRequest
            var httpContextMock = new Mock<HttpContext>();
            var httpRequestMock = new Mock<HttpRequest>();

            var token = new ManagementToken().GetToken(0);
            httpRequestMock.Setup(req => req.Headers["Authorization"]).Returns(token);

            // Asocire HttpRequest la HttpContext
            httpContextMock.Setup(ctx => ctx.Request).Returns(httpRequestMock.Object);

            // Asociere HttpContext la controller
            _feedbackController.ControllerContext = new ControllerContext
            {
                HttpContext = httpContextMock.Object
            };

            // Act
            var result = _feedbackController.Insert(feed);

            // Assert
            result.Should().BeOfType<OkResult>();
        }

        [Fact]
        public void Given_FeedbackExists_When_UpdatingExistingFeedback_Then_FeedbackShouldBeUpdated()
        {
            // Arrange
            // Se pregateste feedback-ul valid de test
            var feedNew = new FeedbackDto(1,0,true);
            var feedOld = new Feedback(1,0,false);

            _feedbackRepositoryMock.Setup(repo => repo.Get(1)).Returns(feedOld);
            _feedbackRepositoryMock.Setup(repo => repo.GetAll()).Returns(new List<Feedback>() { feedOld });
            // HttpContext și HttpRequest
            var httpContextMock = new Mock<HttpContext>();
            var httpRequestMock = new Mock<HttpRequest>();

            var token = new ManagementToken().GetToken(0);
            httpRequestMock.Setup(req => req.Headers["Authorization"]).Returns(token);

            // Asocire HttpRequest la HttpContext
            httpContextMock.Setup(ctx => ctx.Request).Returns(httpRequestMock.Object);

            // Asociere HttpContext la controller
            _feedbackController.ControllerContext = new ControllerContext
            {
                HttpContext = httpContextMock.Object
            };

            // Act
            var result = _feedbackController.Update( feedNew);

            // Assert
            result.Should().BeOfType<OkResult>();
        }

        [Fact]
        public void Given_FeedbackDoesNotExist_When_UpdatingFeedback_Then_NothingShouldHappen()
        {
            // Arrange
            // Se pregateste comment-ul valid de test
            var feedNew = new FeedbackDto(1, 1, true);
            var feedOld = new Feedback(1, 1, false);

            _feedbackRepositoryMock.Setup(repo => repo.Get(1)).Returns(feedOld);

            // HttpContext și HttpRequest
            var httpContextMock = new Mock<HttpContext>();
            var httpRequestMock = new Mock<HttpRequest>();

            var token = new ManagementToken().GetToken(0);
            httpRequestMock.Setup(req => req.Headers["Authorization"]).Returns(token);

            // Asocire HttpRequest la HttpContext
            httpContextMock.Setup(ctx => ctx.Request).Returns(httpRequestMock.Object);

            // Asociere HttpContext la controller
            _feedbackController.ControllerContext = new ControllerContext
            {
                HttpContext = httpContextMock.Object
            };

            // Act
            var result = _feedbackController.Update( feedNew);

            // Assert
            result.Should().BeOfType<NotFoundResult>();
        }

        [Fact]
        public void UnauthorizedAccess_DeleteFeedbackFailed()
        {
            // Arrange
            var feedbackDto = new Feedback(1, 0, true);
            _feedbackRepositoryMock.Setup(repo => repo.Insert(feedbackDto));

            // HttpContext și HttpRequest
            var httpContextMock = new Mock<HttpContext>();
            var httpRequestMock = new Mock<HttpRequest>();

            httpRequestMock.Setup(req => req.Headers["Authorization"]).Returns("");

            // Asocire HttpRequest la HttpContext
            httpContextMock.Setup(ctx => ctx.Request).Returns(httpRequestMock.Object);

            // Asociere HttpContext la controller
            _feedbackController.ControllerContext = new ControllerContext
            {
                HttpContext = httpContextMock.Object
            };

            _feedbackRepositoryMock.Setup(repo => repo.Get(1)).Returns(feedbackDto);

            // Act
            // Am doar un feedback adaugat
            var result = _feedbackController.Delete(new FeedbackDto(1,0,false));

            // Assert
            result.Should().BeOfType<UnauthorizedResult>();
        }

        [Fact]
        public void Given_NewFeedback_When_InsertFeedback_Unauthorized()
        {
            // Arrange
            // Se pregateste feeback-ul valid de test
            var feed = new FeedbackDto(1, 1, true);

            // HttpContext și HttpRequest
            var httpContextMock = new Mock<HttpContext>();
            var httpRequestMock = new Mock<HttpRequest>();

            httpRequestMock.Setup(req => req.Headers["Authorization"]).Returns("");

            // Asocire HttpRequest la HttpContext
            httpContextMock.Setup(ctx => ctx.Request).Returns(httpRequestMock.Object);

            // Asociere HttpContext la controller
            _feedbackController.ControllerContext = new ControllerContext
            {
                HttpContext = httpContextMock.Object
            };

            // Act
            var result = _feedbackController.Insert(feed);

            // Assert
            result.Should().BeOfType<UnauthorizedResult>();
        }

        [Fact]
        public void Given_FeedbackExists_When_UpdatingExistingFeedback_Unauthorized()
        {
            // Arrange
            // Se pregateste feedback-ul valid de test
            var feedNew = new FeedbackDto( 1, 0, true);
            var feedOld = new Feedback(1, 0, false);

            _feedbackRepositoryMock.Setup(repo => repo.Get(1)).Returns(feedOld);

            // HttpContext și HttpRequest
            var httpContextMock = new Mock<HttpContext>();
            var httpRequestMock = new Mock<HttpRequest>();

            httpRequestMock.Setup(req => req.Headers["Authorization"]).Returns("");

            // Asocire HttpRequest la HttpContext
            httpContextMock.Setup(ctx => ctx.Request).Returns(httpRequestMock.Object);

            // Asociere HttpContext la controller
            _feedbackController.ControllerContext = new ControllerContext
            {
                HttpContext = httpContextMock.Object
            };

            // Act
            var result = _feedbackController.Update( feedNew);

            // Assert
            result.Should().BeOfType<UnauthorizedResult>();
        }

        [Fact]
        public void Unauthorized_UpdateFeedback_NoAction()
        {
            // Arrange
            // Se pregateste comment-ul valid de test
            var feedNew = new FeedbackDto( 1, 1, true);
            var feedOld = new Feedback(1, 1, false);

            _feedbackRepositoryMock.Setup(repo => repo.Get(1)).Returns(feedOld);

            // HttpContext și HttpRequest
            var httpContextMock = new Mock<HttpContext>();
            var httpRequestMock = new Mock<HttpRequest>();

            httpRequestMock.Setup(req => req.Headers["Authorization"]).Returns("");

            // Asocire HttpRequest la HttpContext
            httpContextMock.Setup(ctx => ctx.Request).Returns(httpRequestMock.Object);

            // Asociere HttpContext la controller
            _feedbackController.ControllerContext = new ControllerContext
            {
                HttpContext = httpContextMock.Object
            };

            // Act
            var result = _feedbackController.Update(feedNew);

            // Assert
            result.Should().BeOfType<UnauthorizedResult>();
        }
    }
}