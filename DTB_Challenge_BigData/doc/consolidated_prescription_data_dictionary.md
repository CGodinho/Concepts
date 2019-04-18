# Consolidated Prescription

## Description

Proposal dataset for the Daimler challenge. It joins datasets Drugs Physician, Prescription into a single dataset.

The primary data source for these data is the CMS Chronic Conditions Data Warehouse, which contains Medicare Part D PDE records received through the claims submission cut-off date.

This particular dataset is related to year 2015.

Data is presented in CSV format. Not available values are blank.

## Data Dictionary


|Name.                       |Position|Type               | Mandatory | Description|
|----------------------------|--------|-------------------|-----------|------------|
|npi                         |1       |NUMERIC INTEGER(10)|YES        |Unique professional ID assigned by NPPES|
|drug_simple_name            |2       |STRING CHARS       |YES        |The name of the drug filled. This includes both brand names (drugs that have a trademarked name) and generic names (drugs that do not have a trademarked name)|
|drug_generic_name           |3       |STRING CHARS       |YES        |A term referring to the chemical ingredient of a drug rather than the trademarked brand name under which the drug is sold|
|bene_count                  |4       |NUMERIC INTEGER(5) |NO         |The total number of unique Medicare Part D beneficiaries with at least one claim for the drug. Counts fewer than 11 are suppressed and are indicated by a blank.|
|total_claim_count           |5       |NUMERIC INTEGER(5) |NO         |The number of Medicare Part D claims. This includes original prescriptions and refills. Aggregated records based on total_claim_count fewer than 11 are not included in the data file.|
|medical_school              |6       |STRING CHARS       |YES        |Individual professional's medical school|
|drug_opioid_flag            |7       |BOOLEAN (Y/N)      |YES        |A flag indicating whether drugs in this Drug Name/ Generic Name combination are identified as an opioid drug. The list for opioids are based upon drugs included in the Medicare Part D Overutilization Monitoring System (OMS). The list originates from the Centers for Disease Control and Prevention. For additional information on Medicare Part D OMS visit: https://www.cms.gov/Medicare/Prescription-Drug-Coverage/PrescriptionDrugCovContra/RxUtilization.html.|
|drug_long_acting_opioid_flag|8       |BOOLEAN (Y/N)      |YES        |A flag indicating whether drugs in this Drug Name/ Generic Name combination are identified as an long-acting opioid drug. The list for long-acting opioids are based upon drugs included in the Medicare Part D Overutilization Monitoring System (OMS). Those drugs were then identified by the National Center for Injury Prevention and Control. CDC compilation of benzodiazepines, muscle relaxants, stimulants, zolpidem, and opioid analgesics with oral morphine milligram equivalent conversion factors, 2017 version. Available at https://www.cdc.gov/drugoverdose/resources/data.html.|
|drug_antibiotic_flag        |9       |BOOLEAN (Y/N)      |YES        |A flag indicating whether drugs in this Drug Name/ Generic Name combination are identified as an antibiotic drug. The list for antibiotics was created by identifying antibiotic subcategories with the exclusion of the following types of products: tuberculosis agents, antimalarials, topical agents (topical ophthalmic, optic, vaginal, and dermatological agents, etc.).|
|drug_antipsychotic_flag     |10      |BOOLEAN (Y/N)      |YES        |A flag indicating whether drugs in this Drug Name/ Generic Name combination are identified as an antipsychotic drug. The list for antipsychotics was created by identifying antipsychotic subcategories, including first and second generation antipsychotics, as well as antipsychotics included in combination with other drugs, (e.g., OLANZAPINE/FLUOXETINE HCL).|


