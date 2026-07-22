package jp.co.fullness.ec.frontend.infrastructure.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.JdbcTemplate;

import jp.co.fullness.ec.frontend.infrastructure.entity.CustomerEntity;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerMapperTest {

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private DataSource dataSource;

    private Integer customerId;

    @BeforeEach
    void setUp() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        customerId = jdbc.queryForObject(
                "INSERT INTO customer(name, name_kana, address1, address2, " +
                "phone_number, mail_address, username, password) " +
                "VALUES ('山田太郎', 'ヤマダタロウ', '東京都1-1', 'マンション101', " +
                "'09011112222', 'yamada@example.com', 'yamada', " +
                "'$2a$10$dummyhashdummyhashdummyha') RETURNING id",
                Integer.class);
    }

    @Test
    void selectByMailAddress_メールで顧客を取得でき氏名カナも取れる() {
        CustomerEntity c = customerMapper.selectByMailAddress("yamada@example.com");

        assertThat(c).isNotNull();
        assertThat(c.getId()).isEqualTo(customerId);
        assertThat(c.getName()).isEqualTo("山田太郎");
        assertThat(c.getNameKana()).isEqualTo("ヤマダタロウ");
    }

    @Test
    void selectByMailAddress_該当なしはnull() {
        assertThat(customerMapper.selectByMailAddress("none@example.com")).isNull();
    }

    @Test
    void selectById_IDで顧客を取得できる() {
        assertThat(customerMapper.selectById(customerId).getUsername()).isEqualTo("yamada");
    }

    @Test
    void insert_新規登録で採番IDが反映され氏名カナも保存される() {
        CustomerEntity e = CustomerEntity.builder()
                .name("鈴木花子")
                .nameKana("スズキハナコ")
                .address1("大阪府2-2")
                .address2(null)
                .phoneNumber("08033334444")
                .mailAddress("suzuki@example.com")
                .username("suzuki")
                .password("$2a$10$dummyhashdummyhashdummyha")
                .build();

        customerMapper.insert(e);

        assertThat(e.getId()).isNotNull();
        CustomerEntity saved = customerMapper.selectById(e.getId());
        assertThat(saved.getNameKana()).isEqualTo("スズキハナコ");
        assertThat(saved.getMailAddress()).isEqualTo("suzuki@example.com");
    }

    @Test
    void countByMailAddress_重複を件数で判定できる() {
        assertThat(customerMapper.countByMailAddress("yamada@example.com")).isEqualTo(1);
        assertThat(customerMapper.countByMailAddress("none@example.com")).isZero();
    }

    @Test
    void countByUsername_重複を件数で判定できる() {
        assertThat(customerMapper.countByUsername("yamada")).isEqualTo(1);
        assertThat(customerMapper.countByUsername("none")).isZero();
    }
}