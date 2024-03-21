using ServiceLayer.DtoModels;

namespace ServiceLayer.Contracts
{
    public interface IUserService
    {
        IEnumerable<UserDto> GetAll();

        public IEnumerable<string> GetAuthors();

        UserDto? Get(int id);

        void Delete(int entityId);

        void Insert(UserDtoCreateAcount entity);

        int? Login(UserLoginDto entity);

        void UpdatePassword(int id, string pass);

        int? GetUserIdByEmail(string email);

        public void Insert(UserContentCreatorDto entity);

        public void AddToFavorites(int userId, int articleId);

        public void RemoveToFavorites(int userId, int articleId);
    }
}
