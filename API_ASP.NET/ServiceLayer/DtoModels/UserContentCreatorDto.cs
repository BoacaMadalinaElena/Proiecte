using System.Text.Json.Serialization;

namespace ServiceLayer.DtoModels
{
    public class UserContentCreatorDto
    {
        [JsonIgnore]
        public int Id { get; set; }
        public string Name { get; set; }
        public string Email { get; set; }
        public string Password { get; set; }
        public bool IsCreator { get; set; }
        public string? Description { get; set; }
        public string? Link { get; set; }
        public string Img { get; set; }

        public UserContentCreatorDto(int id,string name, string email,string password, bool isCreator, string description,string link , string img)
        {
            Id = id;
            Name = name;
            Email = email;
            Password = password;
            IsCreator = isCreator;
            Description = description;
            Link = link;
            Img = img;
        }

        public UserContentCreatorDto()
        {
        }
    }
}
