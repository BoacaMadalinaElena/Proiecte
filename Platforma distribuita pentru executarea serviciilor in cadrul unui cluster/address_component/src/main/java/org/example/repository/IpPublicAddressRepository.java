package org.example.repository;


import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.example.dto.IpPublicAddressDto;
import java.util.Optional;

@Repository
public interface IpPublicAddressRepository extends JpaRepository<IpPublicAddressDto,String> {
    @Transactional
    public void deleteByIpAndPort(String ip, int port);
    public Optional<IpPublicAddressDto> findByIpAndPort(String ip, int port);
}
