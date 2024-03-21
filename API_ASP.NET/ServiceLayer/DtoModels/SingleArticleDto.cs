namespace ServiceLayer.DtoModels
{
    public class SingleArticleDto
    {
        public int Id { get; set; }
        public string Title { get; set; }
        public DateTime PublicateDate { get; set; }
        public string Author { get; set; }
        public int AuthorId { get; set; }
        public string Content { get; set; }
        public string City { get; set; }
        public DateTime? DateOfTheEvent { get; set; }
        public string Facebook { get; set; }
        public string Twitter { get; set; }
        public string Instagram { get; set; }

        // Categoriile articolului
        public ICollection<string> Categories { get; set; }

        // Imaginile din articol
        public ICollection<ArticleImageDto> ArticleImages { get; set; }

        public ICollection<CommentDto> Comments { get; set; }

        public SingleArticleDto(int id, string title, DateTime publicateDate, string author,int authorId, string content, string city, DateTime? dateOfTheEvent, string facebook, string twitter, string instagram, List<string> categories, List<ArticleImageDto> images, List<CommentDto> comments)
        {
            Id = id;
            Title = title;
            PublicateDate = publicateDate;
            Author = author;
            Content = content;
            City = city;
            DateOfTheEvent = dateOfTheEvent;
            Facebook = facebook;
            Twitter = twitter;
            Instagram = instagram;
            if (categories != null)
                Categories = categories.ToList();
            if(images != null)
                ArticleImages = images.ToList();
            Comments = comments;
            AuthorId = authorId;
        }

        public SingleArticleDto(int id, string title, DateTime publicateDate, int authorId, string author, string content, string city, DateTime? dateOfTheEvent, string facebook, string twitter, string instagram, List<string> categories, List<ArticleImageDto> images, List<CommentDto> comments)
        {
            Id = id;
            Title = title;
            PublicateDate = publicateDate;
            AuthorId = authorId;
            Author = author;
            Content = content;
            City = city;
            DateOfTheEvent = dateOfTheEvent;
            Facebook = facebook;
            Twitter = twitter;
            Instagram = instagram;
            if (categories != null)
                Categories = categories.ToList();
            if(images != null)
                ArticleImages = images.ToList();
            Comments = comments;
        }

        public SingleArticleDto() { }
    }
}