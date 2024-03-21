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

namespace CommentTest.UnitTests
{
    public class CommentServiceTests_Moq
    {
        private CommentService _systemUnderTest;

        private Mock<IRepository<Comment>> _commentRepositoryMock;
        private Mock<IRepository<User>> _userRepositoryMock;
        public CommentServiceTests_Moq()
        {
            _commentRepositoryMock = new Mock<IRepository<Comment>>();
            _userRepositoryMock = new Mock<IRepository<User>>();

            _systemUnderTest = new CommentService(_commentRepositoryMock.Object,_userRepositoryMock.Object);
        }

        [Fact]
        public void DeleteExistingComment_SuccessfullyDeletedComment()
        {
            // Arrange
            var commentDto = new Comment(1,1,"Un nou comentariu",DateTime.Now);
            _commentRepositoryMock.Setup(repo => repo.Insert(commentDto));

            // Act
            // Am doar un feedback adaugat
            _systemUnderTest.Delete(1);

            // Assert
            // Apelez o singurata data si verific daca a fost sters
            _commentRepositoryMock.Verify(repo => repo.Delete(1), Times.Once);
        }

        [Fact]
        public void Given_NewComment_When_InsertComment_Then_Successfully()
        {
            // Arrange
            // Se pregateste comment-ul valid de test
            var feed = new CreateCommentDto(1,1,1,"Un nou comentariu",DateTime.Now);

            // Act
            // Apelez metoda de inserare
            _systemUnderTest.Insert(feed);

            // Assert
            _commentRepositoryMock.Verify(x => x.Insert(It.Is<Comment>(u => (u.UserId == feed.UserId 
            && u.ArticleId == feed.ArticleId && feed.Content == u.Content))), Times.Once);
        }

        [Fact]
        public void Given_CommentExists_When_UpdatingExistingComment_Then_CommentShouldBeUpdated()
        {
            // Arrange
            // Se pregateste comment-ul valid de test
            var commNew = new CreateCommentDto(1, 1, 1, "Un nou comentariu", DateTime.Now);
            var commOld = new Comment(1,1,"Content",DateTime.Now.AddDays(-1));

            _commentRepositoryMock.Setup(repo => repo.Get(1)).Returns(commOld);

            // Act
            // Apelez metoda de inserare
            _systemUnderTest.Update(1,commNew);

            // Assert
            // Verific ca metoda a fost apelata de salvare intrucat nu se apeleaza update din nivelul inferior
            _commentRepositoryMock.Verify(x => x.SaveChanges(), Times.Once);
        }

        [Fact]
        public void Given_CommentDoesNotExist_When_UpdatingComment_Then_NothingShouldHappen()
        {
            // Arrange
            // Se pregateste comment-ul valid de test
            var commNew = new CreateCommentDto(1, 1, 1, "Un nou comentariu", DateTime.Now);
            var commOld = new Comment(1, 1, "Content", DateTime.Now.AddDays(-1));

            _commentRepositoryMock.Setup(repo => repo.Get(1)).Returns(commOld);

            // Act & Assert
            // Apelez metoda de inserare si astept exceptie
            Assert.Throws<Exception>(() => _systemUnderTest.Update(2, commNew));
        }
    }
}