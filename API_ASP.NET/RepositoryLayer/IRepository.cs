using DomainLayer.Models;

namespace RepositoryLayer
{
    // Interfata de manipulre obiecte ce deriveaza BaseEntity
    // CRUD
    public interface IRepository<T> where T : BaseEntity
    {
        // Toate
        IEnumerable<T> GetAll();

        // Dupa id
        T? Get(int id);

        // Inserare entitate noua
        void Insert(T entity);

        // Actualizare entitate
        void Update(T entity);

        // Stergere entitate
        void Delete(int entityId);

        void SaveChanges();

        public int Count();
    }
}
