import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

        // 테스트용 패스워드들
        System.out.println("admin123: " + encoder.encode("admin123"));
        System.out.println("123456: " + encoder.encode("123456"));
        System.out.println("vben: " + encoder.encode("vben"));
    }
}