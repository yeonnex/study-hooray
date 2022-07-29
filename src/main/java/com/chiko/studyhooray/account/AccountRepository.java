package com.chiko.studyhooray.account;

import com.chiko.studyhooray.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByEmail(String email);
    Account findByEmail(String email);

    boolean existsByNickname(String nickname);
}
