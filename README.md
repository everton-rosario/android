Biblioteca de integração PagSeguro para Android
===============================================

Descrição
---------

Versão Beta da biblioteca para plataforma Android.
A lib facilita o processo de checkout via PagSeguro dentro da plataforma Android, garantindo um fluxo de checkout transparente para o usuário final. 

OBSERVAÇÃO: Lib mock, funcionando offline ainda

Requisitos
----------

 - [Android] 2.3.+
 - [GSon]


Instalação
----------

Baixe o [último JAR][1] ou obtenha via Maven:
```xml
<repositories>
  <repository>
    <id>pagseguro-github</id>
    <name>Maven Repository PagSeguro</name>
    <layout>default</layout>
    <url>https://raw.githubusercontent.com/everton-rosario/android/mvn-repo/</url>
  </repository>
</repositories>
  
<dependency>
    <groupId>br.com.uol.ps</groupId>
    <artifactId>library</artifactId>
    <version>0.3</version>
</dependency>
```

Ou configure via Gradle:

```gradle
...
repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url "https://raw.github.com/pagseguro/android/mvn-repo/"
    }
}

...

dependencies {
    ...
    compile 'br.com.uol.ps:library:0.3'
    compile 'com.google.code.gson:gson:+'
    ...
}
```

Exemplo de Uso
--------------

Permissões necessárias para a Lib funcionar.
Adicionar no Android-Manifest.xml
```xml
  <uses-permission android:name="android.permission.INTERNET" />
  <uses-permission android:name="android.permission.READ_PHONE_STATE" />
  <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
  <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```


Chamada de pagamento a ser adicionada:
```java
  PagSeguro.pay(new PagSeguroRequest().withNewItem("Nome do item", 1.0, new BigDecimal(1.00))   // Nome do item, quantidade do item, valor unitário do item
                                      .withVendorEmail("suporte@lojamodelo.com.br")             // Email do vendedor, deverá ser igual ao email da autenticacao
                                      .withBuyerEmail("comprador@mail.com.br")                  // Email do comprador caso possua
                                      .withBuyerCellphoneNumber("5511992190364")                // Telefone do comprador
                                      .withReferenceCode("123")                                 // Codigo que é utilizado apenas pelo vendedor, para referencia de transação
                                      .withEnvironment(PagSeguro.Environment.PRODUCTION)        // Ambiente que será usado: PRODUCTION, FAKE ou SANDBOX
                                      .withAuthorization("weber.astorino@gmail.com", "490F6FC24C0B4AE3B5A363717B34BA39"),
                getActivity(),
                R.id.container,                                                                 // Id do fragment/view onde serão desenhadas as telas de checkout
                new PagSeguro.PagSeguroListener() {
      @Override
      public void onSuccess(PagSeguroResponse response, Context context) {
          Toast.makeText(context, "Lib PS retornou pagamento aprovado!", Toast.LENGTH_LONG).show();
      }
  
      @Override
      public void onFailure(PagSeguroResponse response, Context context) {
          Toast.makeText(context, "Lib PS retornou FALHA no pagamento!", Toast.LENGTH_LONG).show();
      }
  });
```


A adição da Lib e o fluxo de checkout são desenhados dinamicamente na tela do aplicativo em que esteja embarcada. Portanto é necessário enviar o ID do container que deverá ser um Fragment.

Verifique o exemplo antes de alterar seu aplicativo:
```xml
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/container"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity"
    tools:ignore="MergeRootFrame" />
```

Executando a Loja Modelo (Exemplo)
----------------------------------

Para executar o projeto exemplo:
- Fazer clone do repositorio do github
- Importar na sua IDE (Android Studio ou Eclipse)
- Executar o modulo "app"


Dúvidas?
----------
---
Caso tenha dúvidas ou precise de suporte, acesse nosso [fórum].


Changelog
---------
0.3
 - CVV opcional de acordo com configuração no backend do PagSeguro

0.2
 - Versão com conexão para o Backend de produção do PagSeguro
 - Adicionados mecanismos de token para autorização da app/usuário
 - Lib armazena estado durante a troca de contexto ou recriação da activity na troca de orientação do device
 - Adicionado tratamento para lista de itens: .withNewItem("Nome do item", 1.0, new BigDecimal(1.00))

0.1
 - Versão inicial com contratos de interface iniciais
 - Não existe conexão com o servidor do PagSeguro


Licença
-------

Copyright 2014 PagSeguro Internet LTDA.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.


Notas
-----

 - O PagSeguro somente aceita pagamento utilizando a moeda Real brasileiro (BRL).
 - Certifique-se que o email e o token informados estejam relacionados a uma conta que possua o perfil de vendedor ou empresarial.
 - Certifique-se que tenha definido corretamente o charset de acordo com a codificação (ISO-8859-1 ou UTF-8) do seu sistema. Isso irá prevenir que as transações gerem possíveis erros ou quebras ou ainda que caracteres especiais possam ser apresentados de maneira diferente do habitual.
 - Para que ocorra normalmente a geração de logs, certifique-se que o diretório e o arquivo de log tenham permissões de leitura e escrita.


[Dúvidas?]
----------

Em caso de dúvidas mande um e-mail para desenvolvedores@pagseguro.com.br


Contribuições
-------------

Achou e corrigiu um bug ou tem alguma feature em mente e deseja contribuir?

* Faça um fork.
* Adicione sua feature ou correção de bug.
* Envie um pull request no [GitHub].

  [1]: https://raw.githubusercontent.com/pagseguro/android/mvn-repo/br/com/uol/ps/library/0.3/library-0.3.jar
  [Android]: http://www.android.com/
  [GSon]: https://code.google.com/p/google-gson/
  [fórum]: http://forum.pagseguro.uol.com.br/
  [GitHub]: https://github.com/pagseguro/php/
  
  
 

