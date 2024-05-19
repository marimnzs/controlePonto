# CameraX Kotlin Implementation

Controle de ponto é um projeto desenvolvido em Kotlin para facilitar o registro de horários de trabalho dos funcionários. Com ele, os funcionários podem facilmente marcar os dias e horários em que trabalharam, permitindo um registro de ponto eficiente e preciso com base na geolocalização do usuário..

Além disso, o aplicativo possibilita a geração de relatórios detalhados dos pontos batidos, fornecendo aos funcionários uma visão clara e organizada do tempo trabalhado.

## Funcionalidades

- Captura de fotos
- Gravação de vídeos
- Pré-visualização em tempo real

## Tecnologias Utilizadas

- FirebaseAuth
- FirebaseFirestore
- Kotlin
- Android SDK

## Requisitos

- Android Studio 4.1 ou superior
- Dispositivo ou emulador (API 27) ou superior

## Como Executar

1. Clone este repositório para o seu ambiente local:
    ```sh
    git clone https://github.com/marimnzs/controlePonto.git
    ```

2. Abra o projeto no Android Studio.

3. Sincronize o projeto com os arquivos Gradle.

4. Conecte um dispositivo Android ou inicie um emulador.

5. Execute o aplicativo.

## Estrutura do Projeto

- `LoginActivity.kt`: Contém a lógica de login do aplicativo.
- `SignUpActivity.kt`: Contém a lógica de registro do aplicativo.
- `CheckInActivity.kt`: Responsável por gerenciar a os dias e horarios de trabalho cadastrados.
- `Adapter.kt`: Responsável por gerenciar os componentes da RecyclerView.
- `res/`: Contém os recursos do projeto, incluindo layouts, strings e ícones.

## Contribuição

Se você quiser contribuir com este projeto, sinta-se à vontade para abrir uma issue ou enviar um pull request.

## Referências

- [Documentação do Firebase](https://firebase.google.com/docs?hl=pt-br)
- [Documentação do Kotlin](https://kotlinlang.org/docs/home.html)

---

Feito por Marina Menezes ❤️.
