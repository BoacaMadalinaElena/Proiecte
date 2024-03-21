namespace ServiceLayer.DtoModels
{
    public class CheckCodeDto
    {
        public string Email { get; set; }
        public int Code { get; set; }

        public CheckCodeDto(string email, int code)
        {
            Email = email;
            Code = code;
        }

        public CheckCodeDto() { }
    }
}
