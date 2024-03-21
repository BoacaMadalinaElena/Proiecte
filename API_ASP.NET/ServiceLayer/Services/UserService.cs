using DomainLayer.Models;
using RepositoryLayer;
using ServiceLayer.Contracts;
using ServiceLayer.DtoModels;
using System.Text.RegularExpressions;

namespace ServiceLayer.Services
{
    public class UserService : IUserService
    {
        private readonly IRepository<User> _userRepository;
        private readonly IRepository<FavoriteArticle> _favoriteArticle;
        private readonly IRepository<Article> _articleRepository;

        public UserService(IRepository<User> userRepository, IRepository<FavoriteArticle> favoriteArticle, IRepository<Article> articleRepository)
        {
            _userRepository = userRepository;
            _favoriteArticle = favoriteArticle;
            _articleRepository = articleRepository;
        }

        public IEnumerable<UserDto> GetAll()
        {
            foreach (var user in _userRepository.GetAll())
            {
                Console.WriteLine(user);
            }
            return _userRepository.GetAll().Select(u => new UserDto(u.Id, u.Name, u.Email, u.Password, u.IsCreator, u.Description, u.Link, u.Img));
        }

        public UserDto? Get(int id)
        {
            var user = _userRepository.Get(id);

            return user != null ? new UserDto(user.Id, user.Name, user.Email, user.Password, user.IsCreator, user.Description, user.Link, user.Img) : null;
        }

        public void Delete(int entityId)
        {
            _userRepository.Delete(entityId);
        }

        public int? Login(UserLoginDto user)
        {
            var existingUser = GetUserByEmail(user.Email);
            var id = GetUserIdByEmail(user.Email);
            if (existingUser != null)
            {
                if (VerifyPassword(user.Password, existingUser.Password))
                    return id;
            }
            return null;
        }

        public void UpdatePassword(int id, string pass)
        {
            var user = _userRepository.Get(id);
            if (user != null)
            {
                user.Password = HashPassword(pass);
                _userRepository.Update(user);
            }
            else
            {
                throw new Exception("User not found");
            }
        }

        public void Insert(UserDtoCreateAcount entity)
        {
            // Utilizatori cu acest email
            var existingUser = _userRepository.GetAll().Where(a => a.Email == entity.Email);
            // Patern pt email
            string pattern = @"^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$";

            var isValid = Regex.IsMatch(entity.Password, pattern);
            if (existingUser.Count() > 0)
            {
                throw new InvalidOperationException("User with the same email already exists.");
            }
            if (isValid == false)
            {
                throw new InvalidOperationException();
            }

            // Daca nu exista un utilizator cu acest email, putem continua cu inserția
            var user = new User(entity.Name, entity.Email, HashPassword(entity.Password), entity.IsCreator, null, null, null);
            _userRepository.Insert(user);
        }

        public void Insert(UserContentCreatorDto entity)
        {
            // Utilizatori cu acest email
            var existingUser = _userRepository.GetAll().Where(a => a.Email == entity.Email).ToList();
            // Patern pt email
            string pattern = @"^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$";

            var isValid = Regex.IsMatch(entity.Password, pattern);
            if (existingUser.Count() > 0)
            {
                throw new InvalidOperationException("User with the same email already exists.");
            }
            if (isValid == false)
            {
                throw new InvalidDataException();
            }
            var user = new User(entity.Name, entity.Email, HashPassword(entity.Password), entity.IsCreator, entity.Description, entity.Link, entity.Img);
            _userRepository.Insert(user);
        }


        public string HashPassword(string password)
        {
            string salt = BCrypt.Net.BCrypt.GenerateSalt();
            string hashedPassword = BCrypt.Net.BCrypt.HashPassword(password, salt);
            return hashedPassword;
        }

        public bool VerifyPassword(string password, string hashedPassword)
        {
            return BCrypt.Net.BCrypt.Verify(password, hashedPassword);
        }

        public ICollection<Feedback> GetFeedbacks(int id)
        {
            var user = _userRepository.Get(id);
            if (user != null)
                return user.GivenFeedbacks;
            else
                throw new NullReferenceException("There is no person with this id.");
        }

        public int? GetUserIdByEmail(string email)
        {
            var userList = _userRepository.GetAll();

            foreach (var user in userList)
            {
                if (user.Email == email)
                {
                    return user.Id;
                }
            }
            return null;
        }

        public UserLoginDto? GetUserByEmail(string email)
        {
            var userList = _userRepository.GetAll();

            foreach (var user in userList)
            {
                if (user.Email == email)
                {
                    return new UserLoginDto(user.Email, user.Password);
                }
            }
            return null;
        }

        public void AddToFavorites(int userId, int articleId)
        {
            var user = _userRepository.Get(userId);
            var article = _articleRepository.Get(articleId);
            if (user != null && article != null)
            {
                if (user.Favorites == null)
                { user.Favorites = new List<FavoriteArticle>(); }
                user.Favorites.Add(new FavoriteArticle()
                {
                    Article = article,
                    User = user,
                });
                _userRepository.SaveChanges();
                _articleRepository.SaveChanges();
            }
        }


        public void RemoveToFavorites(int userId, int articleId)
        {
            var user = _userRepository.Get(userId);
            var articles = _favoriteArticle.GetAll().Where(a => a.ArticleId == articleId).ToList();
            if (user != null)
            {
                foreach (var article in articles)
                {
                    if (article.ArticleId == articleId)
                    {
                        _favoriteArticle.Delete(article.Id);
                    }
                }
                _favoriteArticle.SaveChanges();
            }
            else
            {
                throw new Exception("The user does not exist.");
            }
        }

        public IEnumerable<string> GetAuthors()
        {
            var authors = _userRepository.GetAll().Where(u => u.IsCreator == true).Select(u => u.Name);
            return authors.ToList().Distinct();
        }
    }
}
