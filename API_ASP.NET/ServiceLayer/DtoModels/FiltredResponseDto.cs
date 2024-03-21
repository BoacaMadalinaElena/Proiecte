namespace ServiceLayer.DtoModels
{
    public class FiltredResponseDto
    {
        public int NumberOfPage { get; set; }
        public ICollection<ShortArticle> ShortArticles {  get; set; }

        public FiltredResponseDto(int numberOfPage, ICollection<ShortArticle> art)
        {
            NumberOfPage = numberOfPage;
            ShortArticles = art;
            // Constructor fără parametri
        }

        public FiltredResponseDto()
        {

        }
    }
}
