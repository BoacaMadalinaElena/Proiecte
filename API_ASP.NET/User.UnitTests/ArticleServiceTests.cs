using Moq;
using FluentAssertions;
using ServiceLayer;
using RepositoryLayer;
using ServiceLayer.Services;
using DomainLayer.Models;
using ServiceLayer.DtoModels;

namespace ArticleTest.UnitTests
{
    public class ArticleServiceTests_Moq
    {
        private ArticleService _systemUnderTest;

        private Mock<IRepository<Article>> _articleRepositoryMock;
        private Mock<IRepository<User>> _userRepositoryMock;
        private Mock<IRepository<ArticleImage>> _articleImageRepositoryMock;
        private Mock<IRepository<Category>> _categoryImageRepositoryMock;
        private Mock<IRepository<Comment>> _commentRepositoryMock;
        private Mock<IRepository<Feedback>> _feedbackRepositoryMock;
        private Mock<IRepository<FavoriteArticle>> _favoriteRepositoryMock;

        public ArticleServiceTests_Moq()
        {
            _articleRepositoryMock = new Mock<IRepository<Article>>();
            _userRepositoryMock = new Mock<IRepository<User>>();
            _articleImageRepositoryMock = new Mock<IRepository<ArticleImage>>();
            _categoryImageRepositoryMock   = new Mock<IRepository<Category>>();
            _commentRepositoryMock = new Mock<IRepository<Comment>>();
            _feedbackRepositoryMock = new Mock<IRepository<Feedback>>();
            _favoriteRepositoryMock = new Mock<IRepository<FavoriteArticle>>();

            _systemUnderTest = new ArticleService(_articleRepositoryMock.Object,
                _userRepositoryMock.Object,
                _articleImageRepositoryMock.Object,
                _categoryImageRepositoryMock.Object,
                _commentRepositoryMock.Object,
                _feedbackRepositoryMock.Object,
                _favoriteRepositoryMock.Object);
        }

        [Fact]
        public void Given_One_User_When_Deleting_User_Should_Delete_User_Successfully()
        {
            // Arrange
            var articleToDelete = new Article();
            _articleRepositoryMock.Setup(repo => repo.Insert(new Article()));
          
            // Act
            // Am doar un user adaugat
            _systemUnderTest.Delete(1);

            // Assert
            // Apelez o singurata data si verific daca a fost sters
            _articleRepositoryMock.Verify(repo => repo.Delete(1), Times.Once);
           
          
        }


        [Fact]
        public void Given_NewArticle_ValidData_When_InsertArticle_Then_Successfully()
        {
            // Arrange
            // Se pregateste articolul valid de test
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

            // Act
            // Apelez metoda de inserare
            _systemUnderTest.Insert(article);

            // Assert
            // Verific ca metoda a fost apelata doar o data si ca email-ul corespunde
            _articleRepositoryMock.Verify(x => x.Insert(It.Is<Article>(u => u.Title == article.Title)), Times.Once);
        }

        [Fact]
        public void Given_ArticleExists_When_UpdatingExistingArticle_Then_ArticleShouldBeUpdated()
        {
            // Arrange
            // Se pregateste comment-ul valid de test
            var articleNew = new CreateArticleDto("Title",DateTime.Now,"Content...","Iasi",null,"","","",1,new List<string>(),new List<ArticleImageDto>());
            var articleOld = new Article("TitleNew",DateTime.Now,1,"ContentNew...","Iasi",null,"","","",new List<Category>(),new List<ArticleImage>());

            _articleRepositoryMock.Setup(repo => repo.Get(1)).Returns(articleOld);

            // Act
            // Apelez metoda de inserare
            _systemUnderTest.Update(1, articleNew);

            // Assert
            // Verific ca metoda a fost apelata de salvare intrucat nu se apeleaza update din nivelul inferior
            _articleRepositoryMock.Verify(x => x.SaveChanges(), Times.Once);
        }

        [Fact]
        public void Given_ArticleDoesNotExist_When_UpdatingArticle_Then_NothingShouldHappen()
        {
            // Arrange
            // Se pregatesc articolele
            var articleNew = new CreateArticleDto("Title", DateTime.Now, "Content...", "Iasi", null, "", "", "", 1, new List<string>(), new List<ArticleImageDto>());
            var articleOld = new Article("TitleNew", DateTime.Now, 1, "ContentNew...", "Iasi", null, "", "", "", new List<Category>(), new List<ArticleImage>());

            _articleRepositoryMock.Setup(repo => repo.Get(1)).Returns(articleOld);

            // Act & Assert
            // Apelez metoda de inserare si astept exceptie
            Assert.Throws<Exception>(() => _systemUnderTest.Update(2, articleNew));
        }


        [Fact]
        //TODO
        public void Given_InputData_When_DataIsSearch_Then_SuccessfullySerchData()
        {
            // Arrange
            // Se pregatesc articolele
            var article1 = new Article("Title1", DateTime.Now.AddDays(-1), 1, "Content New1...", "Iasi", null, "", "", "", new List<Category>(), new List<ArticleImage>());
            var article2 = new Article("Title12", DateTime.Now.AddDays(-6), 2, "Content New2...", "Bucuresti", null, "", "", "", new List<Category>(), new List<ArticleImage>());
            var article3 = new Article("Title2", DateTime.Now.AddMonths(-1), 1, "Content New3...", "Iasi", null, "", "", "", new List<Category>(), new List<ArticleImage>());
            var article4 = new Article("Title21", DateTime.Now.AddMonths(-12), 1, "Content New4...", "Iasi", null, "", "", "", new List<Category>(), new List<ArticleImage>());
            var articles = new List<Article>
            {
                article1,
                article2,
                article3,
                article4
            };

            _articleRepositoryMock.Setup(repo => repo.GetAll()).Returns(articles);

            var filtredResponseDto = new FilterSortConfigDto("Title1",new List<string>() { "" }, new List<string>() { "" }, new List<string>() { "" }, "", "", "");

            // Act 
            var listOfArticle = _systemUnderTest.GetByFormat(1, filtredResponseDto, 0);

            //Assert
            Assert.Equal( 2, listOfArticle.ShortArticles.Count());
        }

        [Fact]
        //TODO
        public void Given_InputData_When_DataIsSearchByCity_Then_SuccessfullySerchData()
        {
            // Arrange
            // Se pregatesc articolele
            var article1 = new Article("Title1", DateTime.Now.AddDays(-1), 1, "Content New1...", "Iasi", null, "", "", "", new List<Category>(), new List<ArticleImage>());
            var article2 = new Article("Title12", DateTime.Now.AddDays(-6), 2, "Content New2...", "Bucuresti", null, "", "", "", new List<Category>(), new List<ArticleImage>());
            var article3 = new Article("Title2", DateTime.Now.AddMonths(-1), 1, "Content New3...", "Iasi", null, "", "", "", new List<Category>(), new List<ArticleImage>());
            var article4 = new Article("Title21", DateTime.Now.AddMonths(-12), 1, "Content New4...", "Iasi", null, "", "", "", new List<Category>(), new List<ArticleImage>());
            var articles = new List<Article>
            {
                article1,
                article2,
                article3,
                article4
            };

            _articleRepositoryMock.Setup(repo => repo.GetAll()).Returns(articles);

            var filtredResponseDto = new FilterSortConfigDto("", new List<string>() { "" }, new List<string>() { "Iasi" }, new List<string>() { "" }, "", "", "");

            // Act 
            var listOfArticle = _systemUnderTest.GetByFormat(1, filtredResponseDto, 0);

            //Assert
            Assert.Equal(3, listOfArticle.ShortArticles.Count());
        }

        [Fact]
        //TODO
        public void Given_ExistingArticles_When_AuthorExists_Then_ReturnArticlesByAuthor()
        {
            // Arrange
            // Se pregatesc articolele
            var article1 = new Article("Title1", DateTime.Now.AddDays(-1), 1, "Content New1...", "Iasi", null, "", "", "", new List<Category>(), new List<ArticleImage>());
            var article2 = new Article("Title12", DateTime.Now.AddDays(-6), 2, "Content New2...", "Bucuresti", null, "", "", "", new List<Category>(), new List<ArticleImage>());
            var article3 = new Article("Title2", DateTime.Now.AddMonths(-1), 2, "Content New3...", "Iasi", null, "", "", "", new List<Category>(), new List<ArticleImage>());
            var article4 = new Article("Title21", DateTime.Now.AddMonths(-12), 1, "Content New4...", "Iasi", null, "", "", "", new List<Category>(), new List<ArticleImage>());
            var articles = new List<Article>
            {
                article1,
                article2,
                article3,
                article4
            };

            _articleRepositoryMock.Setup(repo => repo.GetAll()).Returns(articles);

            var filtredResponseDto = new FilterSortConfigDto("", new List<string>() { "" } , new List<string>() { ""}, new List<string>() { ""}, "", "", "");

            // Act 
            var listOfArticle = _systemUnderTest.GetByAuthor(1,1, filtredResponseDto);

            //Assert
            Assert.Equal(2, listOfArticle.ShortArticles.Count());
        }

        [Fact]
        //TODO
        public void Given_ExistingArticlesById_When_ArticleExists_Then_ReturnArticlesById()
        {
            // Arrange
            // Se pregatesc articolele
            var article1 = new Article("Title1", DateTime.Now.AddDays(-1), 1, "Content New1...", "Iasi", null, "", "", "", new List<Category>(), new List<ArticleImage>());

            _articleRepositoryMock.Setup(repo => repo.Get(1)).Returns(article1);

            // Act 
            var article = _systemUnderTest.Get(1);

            //Assert
            Assert.Equal("Title1", article.Title);
        }

        [Fact]
        //TODO
        public void Given_ExistingArticlesById_When_ArticleDoesNotExist_Then_Null()
        {
            // Arrange
            // Se pregatesc articolele
            var article1 = new Article("Title1", DateTime.Now.AddDays(-1), 1, "Content New1...", "Iasi", null, "", "", "", new List<Category>(), new List<ArticleImage>());

            _articleRepositoryMock.Setup(repo => repo.Get(1)).Returns(article1);

            // Act 
            var article = _systemUnderTest.Get(10);

            //Assert
            Assert.Null(article);
        }
    }
}