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

namespace FeedbackTest.UnitTests
{
    public class FeedbackServiceTests_Moq
    {
        private FeedbackService _systemUnderTest;

        private Mock<IRepository<Feedback>> _feedbackRepositoryMock;

        public FeedbackServiceTests_Moq()
        {
            _feedbackRepositoryMock = new Mock<IRepository<Feedback>>();


            _systemUnderTest = new FeedbackService(_feedbackRepositoryMock.Object);
        }

     

        [Fact]
        public void DeleteExistingFeedback_SuccessfullyDeletedFeedback()
        {
            // Arrange
            var feedbackDto = new Feedback(1, 1, true);
            _feedbackRepositoryMock.Setup(repo => repo.Insert(feedbackDto));
            _feedbackRepositoryMock.Setup(repo => repo.GetAll()).Returns(new[] { feedbackDto });

            // Act
            // Am doar un feedback adaugat
            _systemUnderTest.Delete(new FeedbackDto(1, 1, false));

            // Assert
            // Apelez o singurata data si verific daca a fost sters
            _feedbackRepositoryMock.Verify(repo => repo.Delete(0), Times.Once);
        }

        [Fact]
        public void Given_NewFeedback_When_InsertFeedback_Then_Successfully()
        {
            // Arrange
            // Se pregateste feeback-ul valid de test
            var feed = new FeedbackDto(1,1,true);

            // Act
            // Apelez metoda de inserare
            _systemUnderTest.Insert(feed);

            // Assert
            _feedbackRepositoryMock.Verify(x => x.Insert(It.Is<Feedback>(u => u.CommentId == feed.CommentId)), Times.Once);
        }

        [Fact]
        public void Given_FeedbackExists_When_UpdatingExistingFeedback_Then_FeedbackShouldBeUpdated()
        {
            // Arrange
            // Se pregateste comment-ul valid de test
            var feedNew = new FeedbackDto(1,1,true);
            var feedOld = new Feedback(1,1,false);

            _feedbackRepositoryMock.Setup(repo => repo.Get(0)).Returns(feedOld);
            _feedbackRepositoryMock.Setup(repo => repo.GetAll()).Returns(new List<Feedback>() {feedOld});
            // Act
            // Apelez metoda de inserare
            _systemUnderTest.Update( feedNew);

            // Assert
            // Verific ca metoda a fost apelata de salvare intrucat nu se apeleaza update din nivelul inferior
            _feedbackRepositoryMock.Verify(x => x.SaveChanges(), Times.Once);
        }

        [Fact]
        public void Given_FeedbackDoesNotExist_When_UpdatingFeedback_Then_NothingShouldHappen()
        {
            // Arrange
            // Se pregateste comment-ul valid de test
            var feedNew = new FeedbackDto( 1, 1, true);
            var feedOld = new Feedback(1, 1, false);

            _feedbackRepositoryMock.Setup(repo => repo.Get(1)).Returns(feedOld);

            // Act & Assert
            // Apelez metoda de inserare si astept exceptie
            Assert.Throws<Exception>(() => _systemUnderTest.Update( feedNew));
        }

        [Fact]
        public void Given_ExistingCommentsAndFeedback_When_ExtractFeedbackByCommentId_Then_ReturnAssociatedFeedback()
        {
            // Arrange
            // Se pregateste comment-ul valid de test
            var feed1 = new Feedback( 1, 1, true);
            var feed2 = new Feedback(2, 2, false);
            var list = new List<Feedback>()
            {
                feed1,
                feed2
            };

            _feedbackRepositoryMock.Setup(repo => repo.GetAll()).Returns(list);

            // Act & Assert
            // Apelez metoda de inserare si astept exceptie
            Assert.Equal(1, _systemUnderTest.GetByCommentId(1).Count());
        }
    }
}