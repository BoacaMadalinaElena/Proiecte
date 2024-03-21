using ServiceLayer.SessionVariable;
using Microsoft.IdentityModel.Tokens;

namespace UserTest.UnitTests
{
    public class TokenTests_Moq
    {
        private readonly ManagementToken _underTest ;
        public TokenTests_Moq()
        {
            _underTest = new ManagementToken();
        }

        [Fact]
        public void Given_ValidToken_When_VerifyToken_Then_SuccessfulValidation()
        {
            // Arrange
            var id = 1;
            var token = _underTest.GetToken(id);

            // Act 
            // verificare token
            var idResult = _underTest.IsValid(token);

            // Assert
            Assert.Equal(idResult, id);
        }

        //Given_InvalidToken_When_VerifyToken_Then_UnsuccessfulValidation
        [Fact]
        public void Given_InvalidToken_When_VerifyToken_Then_UnsuccessfulValidation()
        {
            // Arrange
            var id = 1;
            var token = _underTest.GetToken(id);
            token +=  "a";

            // Act & Assert
            //Microsoft.IdentityModel.Tokens.SecurityTokenSignatureKeyNotFoundException
            Assert.Throws<SecurityTokenSignatureKeyNotFoundException>(() =>
            {
                // verificare token care arunca exceptie SecurityTokenSignatureKeyNotFoundException
                _underTest.IsValid(token);
            });
        }
    }
}