# NiftyCarts

NiftyCarts a Fabric port of the mod: [AstikorCarts](https://github.com/issork/astikor-carts).
It is a Minecraft mod that lets you travel, transport goods, and plow fields with horse drawn carts!

## Contributing
### Adding a language - Datagen
Parts of the English and German translations are generated using Minecraft DataGeneration (see [datagen/lang/NiftyCartsEnUsLanguageProvider.java](https://github.com/jmb05/nifty-carts/blob/1.21/src/main/java/net/jmb19905/niftycarts/datagen/lang/NiftyCartsEnUsLanguageProvider.java) and [datagen/lang/NiftyCartsDeDeLanguageProvider.java](https://github.com/jmb05/nifty-carts/blob/1.21/src/main/java/net/jmb19905/niftycarts/datagen/lang/NiftyCartsDeDeLanguageProvider.java)). The generated data is combined with an already exsisting language file which ends in ```.existing.json```. The generated files are placed in [src/main/generated/assets/niftycarts/lang/](https://github.com/jmb05/nifty-carts/tree/1.21/src/main/generated/assets/niftycarts/lang). 
### Adding a language - Manually
To add a language without using datagen use the fully generated [en_us.json](https://github.com/jmb05/nifty-carts/blob/1.21/src/main/generated/assets/niftycarts/lang/en_us.json) as a starting point and place your lang file into [src/main/resources/assets/niftycarts/lang/](https://github.com/jmb05/nifty-carts/tree/1.21/src/main/resources/assets/niftycarts/lang).
