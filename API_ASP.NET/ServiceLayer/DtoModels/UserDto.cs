namespace ServiceLayer.DtoModels
{
    public class UserDto
    {
        public int Id { get; set; }
        public string Name { get; set; }
        public string Email { get; set; }
        public string Password { get; set; }
        public bool IsCreator { get; set; }
        public string? Description { get; set; }
        public string? Link { get; set; }
        public string? Img { get; set; }

        public UserDto(int id,string name, string email,string password, bool isCreator, string description,string link, string img  = null)
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

        public UserDto()
        {
        }
    }
}
