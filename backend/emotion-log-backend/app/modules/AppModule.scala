package modules

import com.google.inject.AbstractModule
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class AppModule extends AbstractModule {

  override def configure(): Unit = {
    // PasswordEncoderインターフェースが要求されたら、
    // BCryptPasswordEncoderクラスのインスタンスを提供するようにバインド(紐付け)する
    bind(classOf[PasswordEncoder])
      .to(classOf[BCryptPasswordEncoder])
      .asEagerSingleton()
  }
}
