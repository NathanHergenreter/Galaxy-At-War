#IGNORE

This directory contains txt files which are used to define a new land unit.
When creating a new land unit entry, follow the below format for proper layout

NOTE: 'i' is for integer, 'd' for double, and 's' for String. Values within ()
are the range the actual value should be within

Format:
type=s	 	      - Name of the unit type
transportType=s	      - Name of the corresponding transport ship type
		      - Leave out if unit is created alongside a ship (ie Marines)
buildTime=i(1-n)      - Number of days required to build unit
cost=i(0-n)	      - Money cost of unit
manpower=i(0-n)       - Manpower cost of unit, also (may in the future) affect recovery
alloys=i(0-n)         - Alloy cost of unit, also (may in the future) affect recovery
hp=i(1000's+)         - Max HP of unit, standard is 10,000
hardness=i(0-9)       - Represents how armored unit is (ie how many vehicles),
		      - increases unit durability
attack=i(0-9)         - Represents unit's effectiveness at attacking (ie applies only on attacks)
defense=i(0-9)        - Represents unit's effectivess at defending (ie applies only on defenses)
boardingAttack=i(0-9) - Represents unit's effectives at boarding (ie only applies in naval combat)
boardingDefense=i(0-9)- Represents unit's effectiveness at defending against boarding
infantryPower=i(0-9)  - Represents effective power of infantry in unit
armorPower=i(0-9)     - Represents effective power of armor (ie tanks, other armored vehicles)
artilleryPower=i(0-9) - Represents effective power of artillery support in unit
airPower=i(0-9)	      - Represents effective power of air support in unit