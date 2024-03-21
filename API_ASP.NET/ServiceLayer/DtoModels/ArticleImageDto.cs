namespace ServiceLayer.DtoModels { 
    public class ArticleImageDto 
    {
        public string Content { get; set; }

        public ArticleImageDto(string content)
        {
            Content = content;
        }

        public ArticleImageDto() { }
    }
}
