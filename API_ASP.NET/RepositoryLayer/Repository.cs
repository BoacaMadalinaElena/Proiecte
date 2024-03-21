using DomainLayer.Context;
using DomainLayer.Models;
using Microsoft.EntityFrameworkCore;

namespace RepositoryLayer
{
    public class Repository<T> : IRepository<T> where T : BaseEntity
    {
        // Contextul
        private readonly BlogDbContext _blogDbContext;
        // Multimea de elemente
        private DbSet<T> entityDbSet;

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="applicationDbContext">Contextul aplicatiei</param>
        public Repository(BlogDbContext applicationDbContext)
        {
            _blogDbContext = applicationDbContext;
            // Multimea de elemente
            entityDbSet = _blogDbContext.Set<T>();
        }   
        

        public IEnumerable<T> GetAll()
        {
            return entityDbSet.AsEnumerable();
        }
        public T? Get(int id)
        {
            // null daca nu e sau sunt mai mult de una
            return entityDbSet.SingleOrDefault(c => c.Id == id);
        }
   
        public void Insert(T entity)
        {
            if (entity == null)
            {
                throw new ArgumentNullException("entity");
            }

            entityDbSet.Add(entity);
            _blogDbContext.SaveChanges();
        }

        public void Delete(int entityId)
        {
            var entity = entityDbSet.SingleOrDefault(c => c.Id == entityId);
            if (entity == null) return;

            entityDbSet.Remove(entity);
            _blogDbContext.SaveChanges();
        }
      
        public void Update(T entity)
        {
            if (entity == null)
            {
                throw new ArgumentNullException("entity");
            }

            entityDbSet.Update(entity);
            _blogDbContext.SaveChanges();
        }  

        public void SaveChanges()
        {
            _blogDbContext.SaveChanges();
        }

        public int Count()
        {
            return entityDbSet.Count();
        }
    }
}
