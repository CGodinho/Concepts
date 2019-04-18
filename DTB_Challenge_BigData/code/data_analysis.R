###############################################################################
# This R script performs a exploratoy dataa nalysis over the 3 provided
#datasets.
###############################################################################

###############################################################################
# Setup
###############################################################################
library(readr)
library(dplyr)
library(stringr)


setwd("C:\\daimler\\data")


###############################################################################
#                 D R U G S      D A T A F R A M E 
###############################################################################
drugs_raw <- read_csv("PartD_Prescriber_PUF_Drug_Ntl_15.csv", quote='"', col_types = cols(.default = "c"))

dim(drugs_raw)
names(drugs_raw)

drugs_filtered <- drugs_raw %>%
  select(`Drug Name`, `Generic Name`, `Opioid Drug Flag`, `Long-Acting Opioid Drug Flag`, `Antibiotic Drug Flag`, `Antipsychotic Drug Flag`)

# check availability
any(is.na(drugs_filtered$`Drug Name`))
any(is.na(drugs_filtered$`Generic Name`))
any(is.na(drugs_filtered$`Opioid Drug Flag`))
any(is.na(drugs_filtered$`Long-Acting Opioid Drug Flag`))
any(is.na(drugs_filtered$`Antibiotic Drug Flag`))
any(is.na(drugs_filtered$`Antipsychotic Drug Flag`))

# count differt vlaues for boolean (Y/N)
table(drugs_filtered$`Opioid Drug Flag`)
table(drugs_filtered$`Long-Acting Opioid Drug Flag`)
table(drugs_filtered$`Antibiotic Drug Flag`)
table(drugs_filtered$`Antipsychotic Drug Flag`)

# Drug name is not a unique key
sum(duplicated(drugs_filtered$`Drug Name`))

# drug name + drup generic name forms a key
sum(duplicated(paste(drugs_filtered$`Drug Name`, drugs_filtered$`Generic Name`)))

# conut distinct duplicates
length(unique(drugs_filtered[duplicated(drugs_filtered$`Drug Name`), ]$'Drug Name'))


###############################################################################
#             P H Y S I C I A N S       D A T A F R A M E 
###############################################################################
# Quotes are used as field delimiter besides the separator
# Reading all data as characters
physicians_raw <- read_csv("Physician_Compare_National_Downloadable_File_2015.csv", quote='"', col_types = cols(.default = "c"))
physicians_raw2 <- read_csv("Physician_Compare_National_Downloadable_File.csv", quote='"', col_types = cols(.default = "c"))

dim(physicians_raw)
names(physicians_raw)

# Prepare a unique dataframe 
physicians_filtered_unique <- physicians_raw %>% 
                              select(NPI, `Medical school name`) %>% 
                              unique()

# Check availability
any(is.na(physicians_filtered_unique$NPI))
any(is.na(physicians_filtered_unique$`Medical school name`))

unique(str_length(physicians_filtered_unique$NPI))

physicians_filtered_unique$NPI <- as.numeric(physicians_filtered_unique$NPI)

# Are the pairs <NPI, First Name, Last Name, Medical school> unique?
(nrow(physicians_filtered_unique %>% group_by(NPI) %>% summarise(count = n()) %>% filter(count > 1)) == 0)
any(duplicated(physicians_filtered_unique$NPI))

# NPI is numeric
is.numeric(physicians_filtered_unique$NPI)


###############################################################################
#            P R E S C R I P T I O N       D A T A F R A M E 
###############################################################################
prescription_raw <- read_tsv("PartD_Prescriber_PUF_NPI_Drug_15.txt", col_types = cols(.default = "c"))

dim(prescription_raw)
names(prescription_raw)

# according to data dictionary, if nppes_provider_first_name is blank,
# data is related to institutions and not particular prescribers,
# so removing this cases
prescription_filtered <- prescription_raw %>% 
                         filter(!is.na(nppes_provider_first_name)) %>%
                         select(npi, drug_name, generic_name, bene_count, total_claim_count)

dim(prescription_filtered)

# percentage of not particualr prescribers
(nrow(prescription_raw) - nrow(prescription_filtered)) / nrow(prescription_raw) * 100

# check availability
any(is.na(prescription_filtered$npi))
any(is.na(prescription_filtered$drug_name))
any(is.na(prescription_filtered$generic_name))
any(is.na(prescription_filtered$bene_count))
sum(is.na(prescription_filtered$bene_count))
any(is.na(prescription_filtered$total_claim_count))

# check key
sum(duplicated(paste(prescription_filtered$npi, prescription_filtered$drug_name, prescription_filtered$generic_name)))

prescription_filtered$npi               <- as.numeric(prescription_filtered$npi)
prescription_filtered$bene_count        <- as.numeric(prescription_filtered$bene_count)
prescription_filtered$total_claim_count <- as.numeric(prescription_filtered$total_claim_count)

table(str_length(prescription_filtered$bene_count))
table(str_length(prescription_filtered$total_claim_count))

# free some memory
rm(prescription_raw)


###############################################################################
# Other Verifications
###############################################################################
# Are all the prescription NPIs in Physicians?
prescription_npi_unique <- unique(prescription_filtered$npi)
length(prescription_npi_unique)
physicians_npi_unique <- physicians_filtered_unique$NPI
length(physicians_npi_unique)

# Check NPIs in prescription only ....
prescription_npi_unique[!prescription_npi_unique %in% physicians_npi_unique]
length(prescription_npi_unique[!prescription_npi_unique %in% physicians_npi_unique])

# Are all the drugs registered in the drugs list?
prescription_drugs_unique <- unique(paste(prescription_filtered$drug_name, prescription_filtered$generic_name))

drugs_name_unique <- paste(drugs_filtered$`Drug Name`, drugs_filtered$`Generic Name`)

length(prescription_drugs_unique)
length(drugs_name_unique)

(length(prescription_drugs_unique[!prescription_drugs_unique %in% drugs_name_unique]) == 0)