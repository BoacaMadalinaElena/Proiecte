namespace ServiceLayer.DtoModels
{
    public class NewPasswordDto
    {
        public string Password { get; set; }

        public NewPasswordDto()
        {
        }

        public NewPasswordDto( string pass)
        {
            Password = pass;
        }
    }
}
