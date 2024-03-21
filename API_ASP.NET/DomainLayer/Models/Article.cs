namespace DomainLayer.Models
{
    public class Article : BaseEntity
    {
        public string Title { get; set; }
        public DateTime PublicateDate { get; set; }

        //id ul autorului ar trebui de tip User sa faca mapare automat
        public int AuthorId { get; set; }

        public string Content { get; set; }
        public string City { get; set; }
        public DateTime? DateOfTheEvent { get; set; }
        public string Facebook { get; set; }
        public string Twitter { get; set; }
        public string Instagram { get; set; }

        // Comentariile din articol
        public ICollection<Comment> Comments { get; set; }

        // Categoriile articolului
        public ICollection<Category> Categories { get; set; }

        // Imaginile din articol
        public ICollection<ArticleImage> ArticleImages { get; set; }

        // Utilizatori ce au la favorite
        public ICollection<FavoriteArticle> UserFavorites { get; set; }


        public Article(string title, DateTime publicateDate, int authorId, string content, string city, DateTime? dateOfTheEvent, string facebook, string twitter, string instagram, ICollection<Category> categoryes, ICollection<ArticleImage> images)
        {
            Title = title;
            PublicateDate = publicateDate;
            AuthorId = authorId;
            Content = content;
            City = city;  
            DateOfTheEvent = dateOfTheEvent;
            Facebook = facebook;
            Twitter = twitter;
            Instagram = instagram;
            Comments = new List<Comment>();
            ArticleImages = images;
            Categories = categoryes;
        }

        public Article() {
            Categories = new List<Category>();
            ArticleImages = new List<ArticleImage>();
        }
    }
}
