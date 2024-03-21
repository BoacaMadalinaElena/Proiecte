using System.Text.Json.Serialization;

namespace ServiceLayer.DtoModels
{
    public class UserDtoCreateAcount
    {
        public string Name { get; set; }
        public string Email { get; set; }
        public string Password { get; set; }
        [JsonIgnore]
        public bool IsCreator { get; set; }

        public UserDtoCreateAcount(string name, string email, string password)
        {
            Name = name;
            Email = email;
            Password = password;
            IsCreator = false;
        }

        public UserDtoCreateAcount()
        {
        }
    }
}
