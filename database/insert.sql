Contexto: Backend Spring Boot rodando em http://localhost:8080. Foram adicionados endpoints de upload de imagem. O backend já serve os arquivos estáticos em /uploads/**.         
                                                                                                                                                                                    
  O que foi implementado no backend:                                                                                                                                                
                                                                                                                                                                                    
  Novos endpoints:                                                                                                                                                                  
                                                                                                                                                                                    
  ┌────────┬────────────────────────┬───────────────┬───────────────────────────────────────────────────┐                                                                           
  │ Método │          Rota          │ Campo do form │                     Resposta                      │                                                                           
  ├────────┼────────────────────────┼───────────────┼───────────────────────────────────────────────────┤                                                                           
  │ POST   │ /ongs/{id}/foto        │ arquivo       │ Objeto Ong completo com logoUrl preenchido        │                                                                           
  ├────────┼────────────────────────┼───────────────┼───────────────────────────────────────────────────┤                                                                           
  │ POST   │ /doadores/{id}/foto    │ arquivo       │ Objeto Doador completo com fotoUrl preenchido     │                                                                           
  ├────────┼────────────────────────┼───────────────┼───────────────────────────────────────────────────┤                                                                           
  │ POST   │ /campanhas/{id}/imagem │ arquivo       │ Objeto Campanha completo com imagemUrl preenchido │                                                                           
  └────────┴────────────────────────┴───────────────┴───────────────────────────────────────────────────┘                                                                           
                                                                                                                                                                                    
  Campos novos nas respostas dos GETs:                                                                                                                                              
  - GET /ongs/{id} → agora retorna logoUrl (ex: "/uploads/ongs/ong-1-uuid.jpg")                                                                                                     
  - GET /doadores/{id} → agora retorna fotoUrl (ex: "/uploads/doadores/doador-1-uuid.jpg")                                                                                          
  - GET /campanhas/{id} → já retornava imagemUrl                                                                                                                                    
                                                                                                                                                                                    
  Como montar a requisição de upload (fetch):                                                                                                                                       
  const formData = new FormData();                                                                                                                                                  
  formData.append("arquivo", arquivoSelecionado); // input.files[0]                                                                                                                 
                                                                                                                                                                                    
  const response = await fetch(`http://localhost:8080/ongs/${idOng}/foto`, {                                                                                                        
    method: "POST",                                                                                                                                                                 
    body: formData,                                                                                                                                                                 
    // NÃO setar Content-Type — o browser define o boundary do multipart automaticamente                                                                                            
  });                                                                                                                                                                               
  const ong = await response.json(); // retorna o objeto atualizado com logoUrl                                                                                                     
                                                                                                                                                                                    
  Validações já feitas no backend (não precisa replicar):                                                                                                                           
  - Formatos aceitos: image/jpeg, image/png, image/webp                                                                                                                             
  - Tamanho máximo: 5MB                                                                                                                                                             
  - Erros retornam 400 com { "message": "..." }                                                                                                                                     
                                                                                                                                                                                    
   O que o frontend precisa fazer:                                                                                                                                                   
                                                                                                                                                                                    
  1. Componente de upload reutilizável — input type="file" accept="image/jpeg,image/png,image/webp" + preview local com URL.createObjectURL(file) antes de enviar                   
  2. OngProfilePage — botão/área de upload da logo, usando POST /ongs/{id}/foto; exibir logoUrl prefixado com http://localhost:8080 (ex: http://localhost:8080/uploads/ongs/...)    
  3. DonorProfilePage — mesma coisa com POST /doadores/{id}/foto e campo fotoUrl                                                                                                    
  4. Criação/edição de campanha — upload opcional da imagem da campanha via POST /campanhas/{id}/imagem após criar/salvar a campanha; exibir imagemUrl                              
                                                                                                                                                                                    
  Atenção: A URL retornada pelo backend é relativa (ex: /uploads/ongs/arquivo.jpg). Para exibir a imagem, concatenar com a base: http://localhost:8080 + logoUrl.                   
                 

 