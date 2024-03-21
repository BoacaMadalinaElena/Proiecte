namespace ServiceLayer.DtoModels
{
    public class CreateArticleDto
    {
        public string Title { get; set; }
        public DateTime? PublicateDate { get; set; }
        public string Content { get; set; }
        public string City { get; set; }
        public DateTime? DateOfTheEvent { get; set; }
        public string Facebook { get; set; }
        public string Twitter { get; set; }
        public string Instagram { get; set; }
        public int AuthorId { get; set; }

        // Categoriile articolului
        public ICollection<string> Categories { get; set; }

        // Imaginile din articol
        public ICollection<ArticleImageDto> ArticleImages { get; set; }

        public CreateArticleDto() { }

        public CreateArticleDto(string title, DateTime? publicateDate, string content, string city, DateTime? dateOfTheEvent, string facebook, string twitter, string instagram, int authorId, ICollection<string> categories, ICollection<ArticleImageDto> articleImages)
        {
            Title = title;
            PublicateDate = publicateDate;
            Content = content;
            City = city;
            DateOfTheEvent = dateOfTheEvent;
            Facebook = facebook;
            Twitter = twitter;
            Instagram = instagram;
            AuthorId = authorId;
            Categories = categories;
            ArticleImages = articleImages;
        }
    }
}