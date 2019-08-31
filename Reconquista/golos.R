library(readr)
library(dplyr)
library(stringr)
library(ggplot2)


setwd("/Users/carlosgodinho/git/Concepts/Reconquista")

df_golos <- read_csv("golos.csv", col_types = cols(.default = "c"))

dim(df_golos)

summary(df_golos)
str(df_golos)

# Em todas as jornadas existiram golos, portanto espera-se uma lista de 1 a 34
sort(unique(df_golos$Jornada))
# O identificador de Jornada pode ser convertido a integer
df_golos$Jornada <- as.integer(df_golos$Jornada)
# Em formato int o dispaly com ordenação é simplificado
sort(unique(df_golos$Jornada))

# Coluna local apenas aceita valore CASA e FORA
any(is.na(df_golos$Local))
table(df_golos$Local)

any(duplicated(unique(df_golos$Data)))

# Existem 17 adversários
length(unique(df_golos$Adversário))
length(unique(df_golos$Golo))

# 1 - Adversários que não marcaram ao Benfica
unique(df_golos$Adversário)[! unique(df_golos$Adversário) %in% (df_golos$Golo)]

# Lista de marcadores sem vazios
any(is.na(df_golos$Marcador))
sort(df_golos$Marcador)

# Minutos no intervalo [0-90]
df_golos$Minutos
df_golos$Minutos <- as.integer(df_golos$Minutos)
range(df_golos$Minutos)

# Minutos extra
df_golos$`Minutos Extra`
df_golos$`Minutos Extra` <- as.integer(df_golos$`Minutos Extra`)
table(df_golos$`Minutos Extra`)

df_golos

# 2 - Golos marcados em prolongamento
View(df_golos %>%
  filter(`Minutos Extra` != 0))


# Lista de assitências, inclui "corte" e "resalto"
df_golos$Assitência

# Caso bola parada não vazio, "
table(df_golos$`Bola Parada`)


df_golos %>%
  filter(!is.na(`Bola Parada`))

# #- penaltys na epoca
View(df_golos %>%
  filter(`Bola Parada` == "penalty"))

# Distribuição do tipo de golo
table(df_golos$`Tipo Golo`)

# Distribuição do local golo
table(df_golos$`Local Golo`)

# Distribuição da prespetiva baliza
table(df_golos$`Prespetiva Baliza`)



# -----------------------------------------------


df_golos_benfica <- df_golos %>%
                    filter(Golo == "Benfica")

df_golos_benfica

ggplot(data=df_golos_benfica, aes(x = df_golos_benfica$Jornada, y = stat_bin))

ggplot(data=df_golos_benfica, aes(x=Jornada, fill="red")) +
  geom_bar(stat="count")
  
  
ggplot(data=df_golos_benfica, aes(x=Jornada, fill=Marcador)) +
  geom_bar(stat="count") 


ggplot(data=df_golos_benfica[df_golos_benfica$Marcador %in% c("Rafa", "Seferovic", "Jonas", "Pizzi") ,], aes(x=Jornada, fill=Marcador)) +
  geom_bar(stat="count") + 
  coord_polar()


ggplot(data=df_golos_benfica[df_golos_benfica$Assitência %in% c("Grimaldo", "Seferovic", "André Almeida", "Pizzi") ,], aes(x=Jornada, fill=Assitência)) +
  geom_bar(stat="count") + 
  coord_polar()


df_golos_benfica

#--------------------------

# Melhores marcadores do Benfica
df_golos_benfica %>%
  group_by(Marcador)  %>%
  summarize(Golos = n()) %>%
  arrange(desc(Golos))


# Mais assistentes do Benfica
df_golos_benfica %>%
  filter(!is.na(Assitência)) %>%
  group_by(Assitência)%>%
  summarize(Assitências = n()) %>%
  arrange(desc(Assitências))

# Mais combinações Marcador-Assitências
View(df_golos_benfica %>%
  filter(!is.na(Assitência)) %>%
  group_by(Marcador, Assitência)%>%
  summarize(Marcador_Assitência = n()) %>%
  arrange(desc(Marcador_Assitência)))



df_golos_benfica

ggplot(data = df_golos_benfica, aes(x = df_golos_benfica$Minutos)) + 
  geom_histogram(binwidth=.5, colour="black", fill="red", breaks=seq(0, 90, by = 5)) +
  geom_vline(aes(xintercept=45), linetype="dashed", size=1, colour="red") +
  scale_x_continuous(breaks = seq(0, 90, 5)) +
  scale_y_continuous(breaks = seq(1, 20, 1))

ggplot(data = df_golos_benfica, aes(x = df_golos_benfica$Minutos)) + 
  geom_histogram(binwidth=.5, colour="black", fill="red", breaks=seq(0, 90, by = 15)) +
  geom_vline(aes(xintercept=45), linetype="dashed", size=1, colour="red") +
  scale_x_continuous(breaks = seq(0, 90, 15)) +
  scale_y_continuous(breaks = seq(1, 30, 1))

#geom_histogram(binwidth = 0.01, breaks=seq(0, 90, by = 5))



codeResult <- function(golos) {
  if (golos > 0) return ("V")
  if (golos < 0) return ("D")
  return("E")
}


calculaPontos <- function(resultado) {
  saida <- 0
  if (resultado == "V") return(3)
  if (resultado == "E") saida <- 1
  if (resultado == "D") saida <- 10
  return(saida)
}


calculaPontos("V")


df_golos

jornadas <- df_golos %>%
            group_by(Jornada) %>%
            summarize(Local = first(Local), Adversário = first(Adversário), Golos_Benfica = sum(Golo == "Benfica"), Golos_Adversário = sum(!(Golo == "Benfica"))) %>%
            mutate(average = Golos_Benfica - Golos_Adversário)




jornadas



jornadas <- bind_cols(jornadas, result = sapply(jornadas$average, codeResult))


# consecutive results
rle(results)$lengths
rle(results)$values

jornadas

ggplot(jornadas) + 
  geom_bar(mapping = aes(x = Jornada, y = Golos_Benfica,fill="red"), stat = "identity", position = "dodge") + 
  geom_bar(mapping = aes(x = Jornada, y = -Golos_Adversário, fill ="white"), stat = "identity", position = "dodge")

