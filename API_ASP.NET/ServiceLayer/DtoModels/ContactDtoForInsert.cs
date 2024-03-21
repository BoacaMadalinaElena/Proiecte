namespace ServiceLayer.DtoModels
{
    public class ContactDtoForInsert
    {
        public string Name { get; set; }
        public string Subject { get; set; }
        public string Message { get; set; }
        public string Email { get; set; }

        public ContactDtoForInsert(string name, string email, string subject, string message)
        {
            Name = name;
            Subject = subject;
            Message = message;
            Email = email;
        }

        public ContactDtoForInsert()
        {

        }
    }
}
