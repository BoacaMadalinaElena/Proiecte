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

namespace UserTest.UnitTests
{
    public class UserServiceTests_Moq
    {
        private UserService _systemUnderTest;

        private Mock<IRepository<User>> _userRepositoryMock;
        private Mock<IRepository<Article>> _articleRepositoryMock;
        private Mock<IRepository<FavoriteArticle>> _favoriteRepositoryMock;

        public UserServiceTests_Moq()
        {
            _userRepositoryMock = new Mock<IRepository<User>>();
            _articleRepositoryMock = new Mock<IRepository<Article>>();
            _favoriteRepositoryMock = new Mock<IRepository<FavoriteArticle>>();

            _systemUnderTest = new UserService(_userRepositoryMock.Object, _favoriteRepositoryMock.Object, _articleRepositoryMock.Object);
        }

        [Fact]
        public void Given_ValidPassword_and_HashValue_When_VerifyPasswordCalled_Then_PasswordValidationSucceeds()
        {
            // Arrange
            var password = "myPassword";
            var hash = _systemUnderTest.HashPassword(password);

            // Act 
            // verficare daca parolele se potrivesc
            bool isPasswordValid = _systemUnderTest.VerifyPassword(password, hash);

            // Assert
            Assert.True(isPasswordValid);   
        }

        [Fact]
        public void Given_InvalidPassword_and_HashValue_When_VerifyPasswordCalled_Then_PasswordValidationError()
        {
            // Arrange
            var password = "myPassword";
            // parola hash + o mica modificare
            var hash = _systemUnderTest.HashPassword(password) + "a";

            // Act 
            // verficare daca parolele se potrivesc
            bool isPasswordValid = _systemUnderTest.VerifyPassword(password, hash);

            // Assert
            Assert.False(isPasswordValid);
        }

        [Fact]
        public void Given_One_User_When_Deleting_User_Should_Delete_User_Successfully()
        {
            // Arrange
            var userToDelete = new User("Ion Popescu", "ionpopescu@gmail.com", "password", false, "", "", "");
            _userRepositoryMock.Setup(repo => repo.Insert(userToDelete));

            // Act
            // Am doar un user adaugat
            _systemUnderTest.Delete(1);

            // Assert
            // Apelez o singurata data si verific daca a fost sters
            _userRepositoryMock.Verify(repo => repo.Delete(1), Times.Once);
        }

        [Fact]
        public void Given_One_User_When_Updating_Password_Should_Update_Password_Successfully()
        {
            // Arrange
            var user = new User("Ion Popescu", "ionpopescu@gmail.com", "password", false, "string", "string", "string");
            var newPass = "newPass";

            _userRepositoryMock.Setup(repo => repo.Get(1)).Returns(user);

            // Act
            _systemUnderTest.UpdatePassword(1, newPass);

            // Assert
            Assert.Equal(_systemUnderTest.VerifyPassword(newPass, user.Password),true);

            _userRepositoryMock.Verify(repo => repo.Update(user), Times.Once);
        }

        [Fact]
        public void Given_One_User_When_Updating_Password_Should_Throw_Exception_When_User_Not_Found()
        {
            // Arrange
            var users = new List<User>();
            users.Add(new User("Ion Popescu", "ionpopescu@gmail.com", "password", false, "string", "string", "string"));
            users.Add(new User("Ion Popescu2", "ionpopescu2@gmail.com", "password2", false, "string", "string", "string"));

            var newPass = "newPass";
            var userIdToUpdate = 10;

            _userRepositoryMock.Setup(repo => repo.GetAll()).Returns(users);

            // Act & Assert
            Assert.Throws<Exception>(() => _systemUnderTest.UpdatePassword(userIdToUpdate, newPass));
        }

        [Fact]
        public void Given_addNewUser_DuplicateEmail_ThrowsException()
        {
            // Arrange
            var user = new User("Ion Popescu Marinescu", "ionpopescu@gmail.com", "password", false, "string", "string", "string");
            var users = new List<User> { user };

            _userRepositoryMock.Setup(repo => repo.GetAll()).Returns(users);

            var user2 = new UserDtoCreateAcount("Name", "ionpopescu@gmail.com", "password");

            // Act & Assert
            Assert.Throws<InvalidOperationException>(() =>
            {
                // exceptie am deja cu acel email
                _systemUnderTest.Insert(user2);
            });
        }

        [Fact]
        public void Given_addNewUser_PasswordInvalid_ThrowsException()
        {
            // Arrange
            var user2 = new UserDtoCreateAcount("Name", "ionpopescu.com", "password");

            // Act & Assert
            Assert.Throws<System.InvalidOperationException>(() =>
            {
                // exceptie am deja cu acel email
                _systemUnderTest.Insert(user2);
            });
        }

        [Fact]
        public void Given_GetAll_User_Should_Successfully()
        {
            // Arrange
            var user1 = new User("Ion Popescu1", "ionpopescu1@gmail.com", "password", false, "string", "string", "string");
            var user2 = new User("Ion Popescu2", "ionpopescu2@gmail.com", "password", false, "string", "string", "string");
            var user3 = new User("Ion Popescu3", "ionpopescu3@gmail.com", "password", false, "string", "string", "string");
            var list = new List<User> { user1, user2, user3 };

            _userRepositoryMock.Setup(repo => repo.GetAll()).Returns(list);

            // Act
            var listGet = _systemUnderTest.GetAll();
            if (listGet != null)
            {
                for (int i = 0; i < listGet.Count(); i++)
                {
                    Assert.Equal(list[i].Email, listGet.ToList()[i].Email);
                }
            }
            else
            {
                Assert.NotNull(listGet);
            }
        }

        [Fact]
        public void Given_NewUser_ValidData_When_InsertUser_Then_Successfully()
        {
            // Arrange
            // Se pregateste user-ul valid de test
            var user = new UserDtoCreateAcount("Ana", "ana@gmail.com", "pass1234A");

            // Act
            // Apelez metoda de inserare
            _systemUnderTest.Insert(user);

            // Assert
            // Verific ca metoda a fost apelata doar o data si ca email-ul corespunde
            _userRepositoryMock.Verify(x => x.Insert(It.Is<User>(u => u.Email == user.Email)), Times.Once);
        }

        [Fact]
        public void Given_NewUserCreator_ValidData_When_InsertUserCreator_Then_Successfully()
        {
            // Arrange
            // Se pregateste user-ul valid de test
            var user = new UserContentCreatorDto(1,"Madalina","madalina@gmail.com","passWord123",true,"O studenta","facebook.com","");

            // Act
            // Apelez metoda de inserare
            _systemUnderTest.Insert(user);

            // Assert
            // Verific ca metoda a fost apelata doar o data si ca email-ul corespunde
            _userRepositoryMock.Verify(x => x.Insert(It.Is<User>(u => (u.Email == user.Email 
            && u.Name == user.Name && u.IsCreator == user.IsCreator && u.Description == user.Description && u.Link == user.Link))), Times.Once);
        }

        [Fact]
        public void Given_ValidCredentials_When_UserAttemptsLogin_Then_LoginSuccessful()
        {
            // Arrange am nevoie de parola criptate
            // Metoda de criptare e sigur ok
            var user1 = new User("Ion Popescu1", "ionpopescu1@gmail.com",_systemUnderTest.HashPassword( "password1"), false, "string", "string", "string");
            var user2 = new User("Ion Popescu2", "ionpopescu2@gmail.com", _systemUnderTest.HashPassword("password2"), false, "string", "string", "string");
            var users = new List<User>();
            var userLogin = new UserLoginDto("ionpopescu1@gmail.com", "password1");
            users.Add(user2 );
            users.Add(user1 );

            _userRepositoryMock.Setup(repo => repo.Insert(user1));
            _userRepositoryMock.Setup(repo => repo.Insert(user2));
            _userRepositoryMock.Setup(repo => repo.GetAll()).Returns(users);

            // Act
            var id = _systemUnderTest.Login(userLogin);

            // Assert daca e null nu sa facut loginul
            Assert.NotEqual(id,null);
        }

        [Fact]
        public void Given_InvalidCredentials_When_UserAttemptsLogin_Then_LoginFailed()
        {
            // Arrange am nevoie de parola criptate
            // Metoda de criptare e sigur ok
            var user1 = new User("Ion Popescu1", "ionpopescu1@gmail.com", _systemUnderTest.HashPassword("password1") + "1", false, "string", "string", "string");
            var user2 = new User("Ion Popescu2", "ionpopescu2@gmail.com", _systemUnderTest.HashPassword("password2") + "1", false, "string", "string", "string");
            var users = new List<User>();
            var userLogin = new UserLoginDto("ionpopescu1@gmail.com", "password1");
            users.Add(user2);
            users.Add(user1);

            _userRepositoryMock.Setup(repo => repo.Insert(user1));
            _userRepositoryMock.Setup(repo => repo.Insert(user2));
            _userRepositoryMock.Setup(repo => repo.GetAll()).Returns(users);

            // Act
            var id = _systemUnderTest.Login(userLogin);

            // Assert daca e null nu sa facut loginul
            Assert.Equal(id, null);
        }

        [Fact]
        public void Given_UserHasNoFavorites_When_AddingItemToFavorites_Then_ItemShouldBeInFavoritesList()
        {
            // Arrange
            var user = new User("Name", "email@gmail.com", "passwordA123", true, "Description...", "link.com", "");

            _userRepositoryMock.Setup(repo => repo.Get(1)).Returns(user);

            var article = new Article("Title", DateTime.Now, 1, "Content...","Iasi", DateTime.Now.AddMonths(2),"","","", new List<Category>(), new List<ArticleImage>());

            _articleRepositoryMock.Setup(repo => repo.Get(1)).Returns(article);

            // Act
            _systemUnderTest.AddToFavorites(1, 1);

            // Assert 
            _userRepositoryMock.Verify(x => x.SaveChanges(), Times.Once);
        }

        [Fact]
        public void Given_UserDoesNotExist_When_AddingItemToFavorites_Then_ItemShouldNotBeAdded()
        {
            // Arrange
            var user = new User("Name", "email@gmail.com", "passwordA123", true, "Description...", "link.com", "");

            _userRepositoryMock.Setup(repo => repo.Get(1)).Returns(user);

            var article = new Article("Title", DateTime.Now, 1, "Content...", "Iasi", DateTime.Now.AddMonths(2), "", "", "", new List<Category>(), new List<ArticleImage>());

            _articleRepositoryMock.Setup(repo => repo.Get(1)).Returns(article);

            // Act
            _systemUnderTest.AddToFavorites(2, 1);

            // Assert 
            _userRepositoryMock.Verify(x => x.SaveChanges(), Times.Never);
            _articleRepositoryMock.Verify(x => x.SaveChanges(), Times.Never);
        }

        [Fact]
        public void Given_ArticleDoesNotExist_When_AddingToFavorite_Then_FavoriteListRemainsUnchanged()
        {
            // Arrange
            var user = new User("Name", "email@gmail.com", "passwordA123", true, "Description...", "link.com", "");

            _userRepositoryMock.Setup(repo => repo.Get(1)).Returns(user);

            var article = new Article("Title", DateTime.Now, 1, "Content...", "Iasi", DateTime.Now.AddMonths(2), "", "", "", new List<Category>(), new List<ArticleImage>());

            _articleRepositoryMock.Setup(repo => repo.Get(1)).Returns(article);

            // Act
            _systemUnderTest.AddToFavorites(1, 2);

            // Assert 
            _userRepositoryMock.Verify(x => x.SaveChanges(), Times.Never);
            _articleRepositoryMock.Verify(x => x.SaveChanges(), Times.Never);
        }

        [Fact]
        public void Given_ItemIsInFavoritesList_When_RemovingItemFromFavorites_Then_ItemShouldNotBeInFavoritesList()
        {
            // Arrange
            var user = new User("Name", "email@gmail.com", "passwordA123", true, "Description...", "link.com", "");

            _userRepositoryMock.Setup(repo => repo.Get(1)).Returns(user);

            var article = new Article("Title", DateTime.Now, 1, "Content...", "Iasi", DateTime.Now.AddMonths(2), "", "", "", new List<Category>(), new List<ArticleImage>());

            _articleRepositoryMock.Setup(repo => repo.Get(1)).Returns(article);

            var favorite = new FavoriteArticle(article,user);

            _favoriteRepositoryMock.Setup(repo => repo.GetAll());

            // Act
            _systemUnderTest.RemoveToFavorites(1, 1);

            // Assert 
            _favoriteRepositoryMock.Verify(x => x.SaveChanges(), Times.Once);
        }

        [Fact]
        public void Given_UserDoesNotExist_When_RemovingItemFromFavorites_Then_NothingShouldHappen()
        {
            // Arrange
            var user = new User("Name", "email@gmail.com", "passwordA123", true, "Description...", "link.com", "");

            _userRepositoryMock.Setup(repo => repo.Get(1)).Returns(user);

            var article = new Article("Title", DateTime.Now, 1, "Content...", "Iasi", DateTime.Now.AddMonths(2), "", "", "", new List<Category>(), new List<ArticleImage>());

            _articleRepositoryMock.Setup(repo => repo.Get(1)).Returns(article);

            var favorite = new FavoriteArticle(article, user); 
            var favorites = new List<FavoriteArticle>() { favorite };

            _favoriteRepositoryMock.Setup(repo => repo.GetAll()).Returns(favorites);

            // Act & assert
            Assert.Throws<Exception>(() => _systemUnderTest.RemoveToFavorites(2, 1));
        }

        [Fact]
        public void Given_RemoveNonExistentArticleFromFavorites_UserExists_NothingShouldHappen()
        {
            // Arrange
            var user = new User("Name", "email@gmail.com", "passwordA123", true, "Description...", "link.com", "");

            _userRepositoryMock.Setup(repo => repo.Get(1)).Returns(user);

            var article = new Article("Title", DateTime.Now, 1, "Content...", "Iasi", DateTime.Now.AddMonths(2), "", "", "", new List<Category>(), new List<ArticleImage>());

            _articleRepositoryMock.Setup(repo => repo.Get(1)).Returns(article);

            var favorite = new FavoriteArticle(article, user);

            _favoriteRepositoryMock.Setup(repo => repo.GetAll());

            // Act
            _systemUnderTest.RemoveToFavorites(1, 2);

            // Assert 
            _favoriteRepositoryMock.Verify(x => x.Delete(It.Is<int>(u => u == 1)), Times.Never);
        }

    }
}