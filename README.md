# UniWay
Progetto ISPW


1. 
modifiche per impostare modalita' full o demo o livello di persistenza file o database
config.properties:


persistence.mode=file   # Possibili valori: 'db' o 'file'


db.url=jdbc:mysql://localhost:3306/yourDatabase


db.username=root


db.password=password


file.path=filepersistenza.txt


running.mode=full   # possibili valori: 'full' o 'demo'


ui.version=fxml #possibili valori: 'fxml' o 'cli'

2.  
impostare la VM come segue:
Run -> Edit Configurations, aggiungere una nuova configurazione
su VM options inserire il seguente comando:
--module-path "path del vostro jfx" --add-modules javafx.controls,javafx.fxml 
