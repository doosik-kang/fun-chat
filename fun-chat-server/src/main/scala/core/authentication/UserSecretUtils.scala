package core.authentication

import core.entities.{CredentialSet, SecuredToken, UserSecret}

object UserSecretUtils {

  def encrypt(secret: UserSecret, securedTokenGenerator: () => SecuredToken): Option[CredentialSet] = {
    val salt = securedTokenGenerator().token
    val password = secret.password.toCharArray
    SecretKeyHashUtils.generate(password, salt)
  }
}
