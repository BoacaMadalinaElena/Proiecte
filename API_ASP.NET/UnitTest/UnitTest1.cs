using Moq;
using FluentAssertions;
using System.Collections.Generic;
using System;
using ServiceLayer;


namespace PetShop.UnitTests
{
    public class ArticleServiceTests_Moq
    {
        private ServiceLayer.Services.ArticleService _systemUnderTest;

        private Mock<IRepository<Pet>> _petsRepositoryMock;
        private Mock<IRepository<PetCategory>> _petsCategoryRepositoryMock;

        public ArticleServiceTests_Moq()
        {
            _petsRepositoryMock = new Mock<IRepository<Pet>>();
            _petsCategoryRepositoryMock = new Mock<IRepository<PetCategory>>();

            _systemUnderTest = new PetService(_petsRepositoryMock.Object, _petsCategoryRepositoryMock.Object);
        }

        [Fact]
        public void Given_new_cat_to_buy_When_there_is_a_dog_in_the_store_Then_should_throw_ConflictException()
        {
            //Arrange
            var dogCategory = new PetCategory()
            {
                Id = 1,
                Name = "Dog",
                Price = 1,
                Quantity = 1,
            };
            var catCategory = new PetCategory()
            {
                Id = 2,
                Name = "Cat",
                Price = 1,
                Quantity = 1,
            };

            var allCategories = new List<PetCategory>()
            {
                dogCategory,
                catCategory
            };

            _petsCategoryRepositoryMock.Setup(repo => repo.GetAll())
                .Returns(allCategories);

            _petsCategoryRepositoryMock.Setup(repo => repo.Get(2))
                .Returns(catCategory);

            var catToBeAdded = new PetDto()
            {
                CategoryId = 2,
                Breed = "Siamese"
            };

            //Act
            Func<int> action = () => _systemUnderTest.IncreasePetSupply(catToBeAdded);

            //Assert
            action.Should().Throw<ConflictException>();

            _petsRepositoryMock.Verify(x => x.Insert(It.IsAny<Pet>()),
                Times.Never);
        }

        [Fact]
        public void Given_new_cat_to_buy_When_there_isnt_a_dog_in_the_store_but_there_are_10_cats_Then_should_throw_OverloadException()
        {
            //Arrange            
            var catCategory = new PetCategory()
            {
                Id = 2,
                Name = "Cat",
                Price = 1,
                Quantity = 11,
            };

            var allCategories = new List<PetCategory>()
            {
                catCategory
            };

            _petsCategoryRepositoryMock.Setup(repo => repo.GetAll())
                .Returns(allCategories);

            _petsCategoryRepositoryMock.Setup(repo => repo.Get(2))
                .Returns(catCategory);

            var catToBeAdded = new PetDto()
            {
                CategoryId = 2,
                Breed = "Siamese"
            };

            //Act
            Func<int> action = () => _systemUnderTest.IncreasePetSupply(catToBeAdded);

            //Assert
            action.Should().Throw<OverloadException>();
            _petsRepositoryMock.Verify(x => x.Insert(It.IsAny<Pet>()),
                Times.Never);
        }

        [Fact]
        public void Given_new_cat_to_buy_When_there_isnt_a_dog_in_the_store_and_are_less_than_10_cats_Then_should_succeed()
        {
            //Arrange
            var allCategories = new List<PetCategory>()
            {
                new PetCategory()
                {
                    Id = 1,
                    Name = "Cat",
                    Price = 1,
                    Quantity = 1,
                }
            };

            _petsCategoryRepositoryMock.Setup(repo => repo.GetAll()).Returns(allCategories);

            var catCategory = new PetCategory()
            {
                Id = 2,
                Name = "Cat",
                Price = 1,
                Quantity = 1,
            };

            _petsCategoryRepositoryMock.Setup(repo => repo.Get(2))
                .Returns(catCategory);

            var catToBeAdded = new PetDto()
            {
                CategoryId = 2,
                Breed = "Siamese",
                Quantity = 2
            };

            _petsRepositoryMock.Setup(repo => repo.Insert(It.IsAny<Pet>())).Returns(100);

            _petsCategoryRepositoryMock.Setup(repo => repo.Update(catCategory));

            //Act
            var result = _systemUnderTest.IncreasePetSupply(catToBeAdded);

            //Assert
            result.Should().Be(100);

            _petsRepositoryMock.Verify(x => x.Insert(It.Is<Pet>(pet => pet.Breed == catToBeAdded.Breed)), Times.Once);

            _petsCategoryRepositoryMock.Verify(x => x.Update(It.Is<PetCategory>(category => category.Id == catCategory.Id && category.Quantity == 3)),
                Times.Once);
        }
    }
}