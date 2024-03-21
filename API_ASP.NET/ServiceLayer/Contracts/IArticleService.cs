using ServiceLayer.DtoModels;

namespace ServiceLayer.Contracts
{
    public partial interface IArticleService
    {
        FiltredResponseDto GetByFormat(int? page,FilterSortConfigDto filterSortConfigDto,int userId);

        FiltredResponseDto GetByAuthor(int authorId,int page, FilterSortConfigDto filterSortConfigDto);

        SingleArticleDto? Get(int id);

        void Delete(int entityId);

        void Insert(CreateArticleDto entity);

        void Update(int id, CreateArticleDto entity);

        public IEnumerable<string>? GetAutors();

        public FiltredResponseDto? GetFavorites(int? page, int userId, FilterSortConfigDto filterSortConfigDto);
    }
}
