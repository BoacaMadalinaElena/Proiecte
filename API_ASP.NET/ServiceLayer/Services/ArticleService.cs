using DomainLayer.Models;
using Microsoft.EntityFrameworkCore;
using RepositoryLayer;
using ServiceLayer.Contracts;
using ServiceLayer.DtoModels;

namespace ServiceLayer.Services
{
    public partial class ArticleService : IArticleService
    {
        // Obiect cu ajutorul caruia se acceseaza nivelurile inferioare
        private readonly IRepository<Article> _articleRepository;
        private readonly IRepository<User> _userRepository;
        private readonly IRepository<ArticleImage> _imageRepository;
        private readonly IRepository<Category> _categoryRepository;
        private readonly IRepository<Comment> _commentRepository;
        private readonly IRepository<Feedback> _feedbackRepository;
        private readonly IRepository<FavoriteArticle> _favoriteRepository;

        #region BaseFunction
        public ArticleService(IRepository<Article> articleRepository, IRepository<User> userRepository, IRepository<ArticleImage> imageRepository, IRepository<Category> cetegoryRepository, IRepository<Comment> commentRepository, IRepository<Feedback> feedbackRepository, IRepository<FavoriteArticle> favoriteRepository)
        {
            _articleRepository = articleRepository;
            _userRepository = userRepository;
            _imageRepository = imageRepository;
            _categoryRepository = cetegoryRepository;
            _commentRepository = commentRepository;
            _feedbackRepository = feedbackRepository;
            _favoriteRepository = favoriteRepository;
        }

        public void Delete(int entityId)
        {
            var articleImageIds = _imageRepository.GetAll()
                .Where(ai => ai.ArticleId == entityId)
                .Select(ai => ai.Id)
                .ToList();

            var categoryIds = _categoryRepository.GetAll()
                .Where(c => c.ArticleId == entityId)
                .Select(c => c.Id)
                .ToList();

            foreach (int i in articleImageIds)
            {
                _imageRepository.Delete(i);
            }

            foreach (var i in categoryIds)
            {
                _categoryRepository.Delete(i);
            }

            _articleRepository.Delete(entityId);
        }

        public void Insert(CreateArticleDto entity)
        {
            IEnumerable<ArticleImage> images;

            if (entity.ArticleImages != null)
            {
                images = entity.ArticleImages.Select(a => new ArticleImage(a.Content));
            }
            else
            {
                images = null;
            }
            var category = entity.Categories;
            entity.PublicateDate = DateTime.Now;
            var article = new Article(entity.Title, (DateTime)entity.PublicateDate, entity.AuthorId, entity.Content, entity.City, entity.DateOfTheEvent, entity.Facebook, entity.Twitter, entity.Instagram, entity.Categories.Select(a => new DomainLayer.Models.Category(a)).ToList(), images.ToList());

            article.ArticleImages = images.ToList();
            article.Categories = entity.Categories.Select(a => new DomainLayer.Models.Category(a)).ToList();
            _articleRepository.Insert(article);
        }

        public void Update(int id, CreateArticleDto entity)
        {
            var existingArticle = _articleRepository.Get(id);

            if (existingArticle != null)
            {
                existingArticle.Title = entity.Title;
                existingArticle.PublicateDate = DateTime.Now;
                existingArticle.AuthorId = entity.AuthorId;
                existingArticle.Content = entity.Content;
                existingArticle.City = entity.City;
                existingArticle.DateOfTheEvent = entity.DateOfTheEvent;
                existingArticle.Facebook = entity.Facebook;
                existingArticle.Twitter = entity.Twitter;
                existingArticle.Instagram = entity.Instagram;
                existingArticle.ArticleImages = entity.ArticleImages.Select(a => new ArticleImage(a.Content)).ToList();
                existingArticle.Categories = entity.Categories.Select(a => new DomainLayer.Models.Category(a)).ToList();
                _articleRepository.SaveChanges();
            }
            else
            {
                throw new Exception("Article don't exist");
            }
        }
        #endregion

        public IEnumerable<string>? GetAutors()
        {
            var listArticle = _articleRepository.GetAll().ToList();
            var listId = listArticle.Select(a => a.AuthorId).ToList();
            var listUser = _userRepository.GetAll().Select(a => (Name: a.Name, Id: a.Id)).ToList();
            var listName = new List<string>();

            foreach (var item in listUser)
            {
                if (listId.Contains(item.Id))
                {
                    listName.Add(item.Name);
                }
            }
            return listName.Distinct();
        }

        public SingleArticleDto? Get(int id)
        {
            var article = _articleRepository.Get(id);
            var images = _imageRepository.GetAll().Where(a => a.ArticleId == id).Select(a => new ArticleImageDto(a.Content)).ToList();
            var categories = _categoryRepository.GetAll().Where(a => a.ArticleId == id).Select(a => a.Name).ToList();
            var comments = _commentRepository.GetAll()
                .Where(a => a.ArticleId == id)
                .ToList()
                .Join(
                    _userRepository.GetAll(),
                    comment => comment.UserId,
                    user => user.Id,
                    (comment, user) => new
                    {
                        Comment = comment,
                        AuthorName = user.Name
                    }
                )
                .ToList()
                .Select(c =>
                {
                    var feedbacks = _feedbackRepository.GetAll()
                        .Where(f => f.CommentId == c.Comment.Id)
                        .Select(f => new FeedbackDto(f.CommentId, f.UserId, f.IsLike))
                        .ToList();

                    return new CommentDto(c.Comment.Id, c.Comment.UserId, c.Comment.ArticleId, c.AuthorName, c.Comment.Content, c.Comment.CreatedAt, feedbacks);
                })
                .ToList();
            List<FeedbackDto> feedbacks;
            foreach (var comment in comments)
            {
                feedbacks = new List<FeedbackDto>();
                var myList = _feedbackRepository.GetAll().Where(a => a.CommentId == comment.Id).Select(f => new FeedbackDto(f.CommentId, f.UserId, f.IsLike)).ToList();
                foreach (var f in myList)
                {
                    feedbacks.Add(f);
                }
                comment.Feedbacks = feedbacks;
            }
            if (article != null)
            {
                var userList = _userRepository.GetAll().ToList();
                var authorUser = userList.FirstOrDefault(user => user.Id == article.AuthorId);
                var author = authorUser?.Name ?? "";

                return new SingleArticleDto(article.Id, article.Title, article.PublicateDate, article.AuthorId, author, article.Content, article.City, article.DateOfTheEvent, article.Facebook, article.Twitter, article.Instagram, categories, images, comments.ToList());
            }
            else
            {
                return null;
            }
        }

        public FiltredResponseDto GetByFormat(int? page, FilterSortConfigDto filterSortConfigDto, int userId)
        {
            int itemsPerPage = 6;
            // articole omise
            int skipItems = 0;
            if (page != null)
            {
                skipItems = ((int)page - 1) * itemsPerPage;
            }
            var articles = _articleRepository.GetAll().ToList();
            var favoritesId = _favoriteRepository.GetAll().Where(a => a.User.Id == userId).Select(a => a.Article.Id);

            // Articoele cu toate campurile
            var allArticlesDto = FunctionGetFullListArticle(articles);

            // Cautare in articole
            var combinedArticles = FunctionSerchArticle(allArticlesDto, filterSortConfigDto);

            var flag = filterSortConfigDto.OptionsCategory.Contains("");
            var filteredArticlesCategory = combinedArticles
                .Where(article => (filterSortConfigDto.OptionsCategory.All(category =>
                    article.Categories.Contains(category)) || flag))
                .ToList();

            // un oras din lista
            flag = filterSortConfigDto.OptionsCity.Contains("");
            var filteredArticlesCity = filteredArticlesCategory.
                Where(article => (filterSortConfigDto.OptionsCity.Contains(article.City) || flag)).ToList();

            // un autor din lista
            flag = filterSortConfigDto.OptionsAuthor.Contains("");
            var filteredArticlesAuthor = filteredArticlesCity.
                Where(article => (filterSortConfigDto.OptionsAuthor.Contains(article.Author) || flag)).ToList();

            List<SingleArticleDto>? filtredArticleEventDate = filterSortConfigDto.OptionsEventDate switch
            {
                "This week" => filteredArticlesAuthor.
                                        Where(a => (a.DateOfTheEvent == null ? 0 : DifferenceInWeeks((DateTime)a.DateOfTheEvent)) <= 1).ToList(),
                "In 1 month" => filteredArticlesAuthor.
                                        Where(a => (a.DateOfTheEvent == null ? 0 : DifferenceInMonths((DateTime)a.DateOfTheEvent)) <= 1).ToList(),
                "In 3 months" => filteredArticlesAuthor.
                                        Where(a => (a.DateOfTheEvent == null ? 0 : DifferenceInMonths((DateTime)a.DateOfTheEvent)) <= 3).ToList(),
                "In 6 months" => filteredArticlesAuthor.
                                        Where(a => (a.DateOfTheEvent == null ? 0 : DifferenceInMonths((DateTime)a.DateOfTheEvent)) <= 6).ToList(),
                "In 1 year" => filteredArticlesAuthor.
                                        Where(a => (a.DateOfTheEvent == null ? 0 : DifferenceInYears((DateTime)a.DateOfTheEvent)) <= 1).ToList(),
                _ => filteredArticlesAuthor,// sunt toate
            };

            List<SingleArticleDto>? filtredArticlePostDate = filterSortConfigDto.OptionsPostDate switch
            {
                "Last 24h" => filtredArticleEventDate.
                                        Where(a => DifferenceInHours(a.PublicateDate) <= 24).ToList(),
                "Last 3 days" => filtredArticleEventDate.
                                        Where(a => DifferenceInDays(a.PublicateDate) <= 3).ToList(),
                "Last 7 days" => filtredArticleEventDate.
                                        Where(a => DifferenceInDays(a.PublicateDate) <= 7).ToList(),
                "Last 14 days" => filtredArticleEventDate.
                                        Where(a => DifferenceInDays(a.PublicateDate) <= 14).ToList(),
                "Last month" => filtredArticleEventDate.
                                        Where(a => DifferenceInMonths(a.PublicateDate) <= 1).ToList(),
                "Last year" => filtredArticleEventDate.
                                        Where(a => DifferenceInYears(a.PublicateDate) <= 1).ToList(),
                _ => filtredArticleEventDate,// sunt toate
            };

            //cheia de sortare e data
            List<SingleArticleDto>? sortedArticle = filterSortConfigDto.OptionsSortedBy switch
            {
                "Event date ascending" => filtredArticlePostDate.OrderBy(a => a.DateOfTheEvent).ToList(),
                "Event date descending" => filtredArticlePostDate.OrderByDescending(a => a.DateOfTheEvent).ToList(),
                "Publish date descending" => filtredArticlePostDate.OrderByDescending(a => a.PublicateDate).ToList(),
                //Publish date ascending
                _ => filtredArticlePostDate.OrderByDescending(a => a.PublicateDate).ToList(),
            };

            var listOfShort = sortedArticle.Select(a => new ShortArticle(a.Id, a.Title, a.PublicateDate, a.Author, a.AuthorId, a.Content, a.ArticleImages != null && a.ArticleImages.Count() != 0 ? a.ArticleImages.ToList()[0] : new ArticleImageDto(), false)).ToList();

            foreach (var article in listOfShort)
            {
                if (favoritesId.Contains(article.Id))
                {
                    article.IsFavorite = true;
                }
            }

            if (page != null)
            {
                return new FiltredResponseDto((int)Math.Ceiling((double)sortedArticle.Count / 6), listOfShort
                .Skip(skipItems)
                .Take(itemsPerPage)
                .ToList());
            }
            else
            {
                return new FiltredResponseDto(listOfShort.Count, listOfShort.ToList());
            }
        }

        public double DifferenceInHours(DateTime dateTime)
        {
            TimeSpan differenceInHours = DateTime.Now - dateTime;
            double hours = differenceInHours.TotalHours;
            return Math.Abs(hours);
        }

        public int DifferenceInDays(DateTime dateTime)
        {
            TimeSpan differenceInDays = DateTime.Now - dateTime;
            int days = differenceInDays.Days;
            return Math.Abs(days);
        }

        public int DifferenceInWeeks(DateTime dateTime)
        {
            TimeSpan differenceInWeeks = DateTime.Now - dateTime;
            int weeks = differenceInWeeks.Days / 7; // Deoarece o săptămână are 7 zile
            return Math.Abs(weeks);
        }

        public int DifferenceInMonths(DateTime dateTime)
        {
            TimeSpan differenceInWeeks = DateTime.Now - dateTime;
            int months = (DateTime.Now.Month - dateTime.Month) + 12 * (DateTime.Now.Year - dateTime.Year);
            return Math.Abs(months);
        }

        public int DifferenceInYears(DateTime dateTime)
        {
            int years = DateTime.Now.Year - dateTime.Year;
            return Math.Abs(years);
        }

        public FiltredResponseDto? GetFavorites(int? page, int userId, FilterSortConfigDto filterSortConfigDto)
        {
            var articles = GetByFormat(null, filterSortConfigDto, userId).ShortArticles;
            var user = _userRepository.Get(userId);
            int itemsPerPage = 6;
            // articole omise
            int skipItems = 0;
            if (page != null)
            {
                skipItems = ((int)page - 1) * itemsPerPage;
            }

            if (user != null)
            {
                var favoritesId = _favoriteRepository.GetAll().Where(a => a.User.Id == userId).Select(a => a.Article.Id).ToList();
                if (favoritesId != null)
                {
                    var myArticle = articles.Where(a => favoritesId.Contains(a.Id)).ToList();

                    return new FiltredResponseDto((int)Math.Ceiling((double)myArticle.Count / 6), myArticle
                        .Skip(skipItems)
                        .Take(itemsPerPage)
                        .ToList());
                }
            }
            return null;
        }

        public FiltredResponseDto GetByAuthor(int authorId, int page, FilterSortConfigDto filterSortConfigDto)
        {
            int itemsPerPage = 6;
            // articole omise
            int skipItems = 0;
            if (page != null)
            {
                skipItems = ((int)page - 1) * itemsPerPage;
            }
            var result = GetByFormat(null, filterSortConfigDto, authorId).ShortArticles;
            if (result != null)
            {
                var result2 = result.Where(a => a.AuthorId == authorId);
                return new FiltredResponseDto((int)Math.Ceiling((double)result2.Count() / 6), result2
                        .Skip(skipItems)
                        .Take(itemsPerPage)
                        .ToList());
            }
            return new FiltredResponseDto();
        }

        private List<SingleArticleDto> FunctionSerchArticle(List<SingleArticleDto> allArticlesDto, FilterSortConfigDto filterSortConfigDto)
        {
            var searchArticleByTitle = allArticlesDto.Where(a => a.Title.ToLower().Contains(filterSortConfigDto.Search.ToLower())).OrderBy(a => a.PublicateDate).ToList();
            var searchArticle = allArticlesDto.Where(a => a.Content.ToLower().Contains(filterSortConfigDto.Search.ToLower())).OrderBy(a => a.PublicateDate).ToList();
            // cel putin un element din categorie
            var combinedArticles = searchArticleByTitle.Union(searchArticle).ToList();
            return combinedArticles;
        }

        private List<SingleArticleDto> FunctionGetFullListArticle(List<Article> articles)
        {
            var allArticlesDto = new List<SingleArticleDto>();
            foreach (var article in articles)
            {
                var images = _imageRepository.GetAll()
                    .Where(a => a.ArticleId == article.Id)
                    .Select(a => new ArticleImageDto(a.Content))
                    .ToList();

                var categories = _categoryRepository.GetAll()
                    .Where(a => a.ArticleId == article.Id)
                    .Select(a => a.Name)
                    .ToList();

                var comments = _commentRepository.GetAll()
                    .Where(a => a.ArticleId == article.Id)
                    .ToList();

                var commentsWithUsers = comments
                    .Join(
                        _userRepository.GetAll(),
                        comment => comment.UserId,
                        user => user.Id,
                        (comment, user) => new CommentDto(comment.Id, comment.UserId, comment.ArticleId, user.Name, comment.Content, comment.CreatedAt, null)
                    )
                    .ToList();
                var commentIds = comments.Select(c => c.Id).ToList(); // Lista cu Id-urile comentariilor

                var feedbacks = _feedbackRepository.GetAll()
                    .Where(f => commentIds.Contains(f.CommentId)) //  doar feedback-urile asociate comentariilor din articolul curent
                    .Select(f => new FeedbackDto(f.CommentId, f.UserId, f.IsLike))
                    .ToList();

                // grupare feedback-urile dupa CommentId pentru a le adauga la comentariile corespunzătoare
                var feedbacksGrouped = feedbacks.GroupBy(f => f.CommentId).ToDictionary(g => g.Key, g => g.ToList());

                // Setare feedback-urile pentru fiecare comentariu
                foreach (var comment in comments)
                {
                    if (feedbacksGrouped.TryGetValue(comment.Id, out var commentFeedbacks))
                    {
                        comment.Feedbacks = commentFeedbacks.Select(a => new Feedback(a.CommentId, a.UserId, a.IsLike)).ToList();
                    }
                }
                var userList = _userRepository.GetAll();
                var authorUser = userList.FirstOrDefault(user => user.Id == article.AuthorId);
                var author = authorUser?.Name ?? "";
                // Adaugam obiectul SingleArticleDto in lista finala
                allArticlesDto.Add(new SingleArticleDto(article.Id, article.Title, article.PublicateDate, author, article.AuthorId, article.Content, article.City, article.DateOfTheEvent, article.Facebook, article.Twitter, article.Instagram, categories, images,
                   comments.Select(a => new CommentDto(a.Id, a.UserId, a.Article, "", a.Content, a.CreatedAt,
                    a.Feedbacks != null ? a.Feedbacks.Select(f => new FeedbackDto(f.CommentId, f.UserId, f.IsLike)).ToList() : new List<FeedbackDto>()
                )).ToList()));
            }
            return allArticlesDto;
        }
    }
}
