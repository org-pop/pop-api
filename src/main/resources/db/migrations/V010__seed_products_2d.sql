-- Seed de produtos das franquias "2°D" (ordem alfabética) e "J&F" (Rodolfo e
-- Diogo, de propósito fora da ordenação alfabética, adicionados ao final).
--
-- price do Rodolfo/Diogo: no JSON de origem o valor era 99999999999999.90, mas
-- a coluna product.price é DECIMAL(10,2) (máx. 99999999.99) — usamos o teto da
-- coluna em vez de alterar o schema.
INSERT INTO product (name, description, price, stock, image_url, franchise, rarity)
VALUES
    ('Funko Pop! Arakaki', 'Namorado da Ana Clara Blefari. (Japonês do cabelo cacheado?)', 670.67, 676767, 'arakaki', '2°D', 'AURUDO'),
    ('Funko Pop! Arthur Machado', 'Arthur Machado de óculos. "Ref"', 99.90, 60, 'arthur', '2°D', 'COMUM'),
    ('Funko Pop! Benfica', 'Gabi fofinha de vestido floral e laçinho no cabelo. (vai em 2 shows so esse ano :0)', 99.90, 60, 'gabi', '2°D', 'COMUM'),
    ('Funko Pop! Boregio', 'Namorado da Camilly. "Isepe", ou "Mezini", ou "Battatini"...ele atende por todos.', 670.67, 676767, 'boregio', '2°D', 'AURUDO'),
    ('Funko Pop! Cadu', 'Cadu no modo Swifter. (cadu''s version)', 99.90, 60, 'cadu', '2°D', 'COMUM'),
    ('Funko Pop! Davi Oliveira', 'Davi no modo PicPayLover. (modo sério)', 99.90, 60, 'davi', '2°D', 'COMUM'),
    ('Funko Pop! Duda Rosa', 'Melhor representante. Vote Duda rosa para presidente do Brasil (ou não, rs).', 99.90, 60, 'duda', '2°D', 'COMUM'),
    ('Funko Pop! Edu Passos', 'Gêmeo um, cabelo mais cortado. Mais velho', 99.90, 60, 'edu_passos', '2°D', 'COMUM'),
    ('Funko Pop! Edu Rodrigues', 'Edu com camiseta de anime. Tiozão.', 99.90, 60, 'edu_rodrigues', '2°D', 'COMUM'),
    ('Funko Pop! Edward', 'Docker Compose vestido de Edward (avenger)', 670.67, 676767, 'edward', '2°D', 'AURUDO'),
    ('Funko Pop! Giulia', 'Giulia doidinha. ownt mainha', 99.90, 60, 'giulia', '2°D', 'COMUM'),
    ('Funko Pop! Igor', 'Quinto guitarrista de visual all-black. (tem uma banda)', 99.90, 60, 'igor', '2°D', 'COMUM'),
    ('Funko Pop! João Maldo', 'João de jaqueta college, valentão no high school. "passa o dinheiro do lanche, novato!"', 99.90, 60, 'joao_maldo', '2°D', 'COMUM'),
    ('Funko Pop! Jones', 'Jones sensualizando com a mão no queixo. "ops, sensualizei...rs!"', 99.90, 60, 'jones', '2°D', 'COMUM'),
    ('Funko Pop! Jorginho', 'Maior participante das aulas de engenharia. Vibe coder natoe fã do orelha', 99.90, 60, 'jorge', '2°D', 'COMUM'),
    ('Funko Pop! Kogake', 'Na China que comem barata, não no Japão. Japones do cabelo liso!', 99.90, 60, 'kogake', '2°D', 'COMUM'),
    ('Funko Pop! Lacerda', 'Hiperativo disfarçado. (Fala mandarim)', 99.90, 60, 'lacerda', '2°D', 'COMUM'),
    ('Funko Pop! Lolo', 'Bateria viciada. Soninho frequente', 670.67, 676767, 'lorraine', '2°D', 'AURUDO'),
    ('Funko Pop! Lucas Lima', 'Campeão Mundial de tênis de mesa. (ele sempre está ouvindo...)', 99.90, 60, 'lucas', '2°D', 'COMUM'),
    ('Funko Pop! Luiza', 'Databricks e sql girl.', 99.90, 60, 'luiza', '2°D', 'COMUM'),
    ('Funko Pop! May', 'Linha de costura no lugar do cabelo. (baunilha???)', 99.90, 60, 'may', '2°D', 'COMUM'),
    ('Funko Pop! Rafa', 'Gêmeo dois, cabelo maior. (lhg)', 99.90, 60, 'rafa', '2°D', 'COMUM'),
    ('Funko Pop! Sousa', 'Calça skinny man.', 99.90, 60, 'sousa', '2°D', 'COMUM'),
    ('Funko Pop! Taina', 'Tainá Tainá Incrivel Como Tu Muda. (ela odeia essa musica kkkkk)', 99.90, 60, 'taina', '2°D', 'COMUM'),
    ('Funko Pop! Rodolfo 01', 'Rodolfo professor de DAD. Ama K-POP, reações coreanas e projetos incríveis!', 99999999.99, 1, 'rodolfo', 'J&F', 'GOD'),
    ('Funko Pop! Diogo 02', 'Diogo é um cara gente boa, professor de DS e Bahia! ', 99999999.99, 1, 'diogo', 'J&F', 'GOD');
