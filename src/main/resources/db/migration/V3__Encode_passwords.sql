create extension if not exists pgcrypto;

--дополнительное шифрование для существовавших паролей
update usr set password = crypt(password, gen_salt('bf', 8));