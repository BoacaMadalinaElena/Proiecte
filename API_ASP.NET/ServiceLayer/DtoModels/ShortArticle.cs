namespace ServiceLayer.DtoModels
{
    public class ShortArticle 
    {
        public int Id { get; set; }
        public string Title { get; set; }
        public DateTime? PublicateDate { get; set; }
        public string Author { get; set; }
        public string Content { get; set; }
        public int AuthorId { get; set; }
        public ArticleImageDto? Img { get; set; }
        // Imaginile din articol
        public bool IsFavorite { get; set; }
        public ShortArticle(int id, string title, DateTime? publicateDate, string author,int authorId, string content, ArticleImageDto?  img,bool isFavorite)
        {
            Id = id;
            Title = title;
            PublicateDate = publicateDate;
            Author = author;
            Content = content;
            Img = img;
            AuthorId = authorId;
            IsFavorite = isFavorite;
        }
    }
}
