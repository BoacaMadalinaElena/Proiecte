namespace DomainLayer.Models
{
    public class User : BaseEntity
    {
        public string Name { get; set; } // minim 3 caractere alfanumerice + speciale 
        public string Email { get; set; } // email valid
        public string Password { get; set; } // se valideaza sus(ajunge criptata aici)
        public bool IsCreator { get; set; } // nu are constrangere
        public string? Description { get; set; } // nu are constrangere

        public string? Link { get; set; } // nu are constrangere
        public string Img { get; set; } // nu are constrangere

        // Articole favorite
        public ICollection<FavoriteArticle> Favorites { get; set; }
        // Feedback dat de user
        public ICollection<Feedback> GivenFeedbacks { get; set; }

        public User(string name, string email, string password, bool isCreator, string description, string link, string img)
        {
            Name = name;
            Email = email;
            Password = password;
            IsCreator = isCreator;
            Description = description;
            Link = link;
            if(img != null)
            {
                Img = img;
            }
            Favorites = new List<FavoriteArticle>();
            GivenFeedbacks = new List<Feedback>();
        }
    }
}
