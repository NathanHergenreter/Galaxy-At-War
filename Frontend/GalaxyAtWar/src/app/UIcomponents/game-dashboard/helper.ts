export class Helper {
    setupAvatarPlanetSprites(gameInstance){
        gameInstance.dirLine1.visible = false;
        gameInstance.dirLine2.visible = false;
        gameInstance.avatarPlanet.setScale(0.6);
        gameInstance.avatarPlanet.visible = false;
        gameInstance.movablePlanet.visible = false;
        gameInstance.movablePlanet.setInteractive();
        gameInstance.movablePlanetArc.setStrokeStyle(2, 0x6666ff);
        gameInstance.movablePlanetArc.visible = false;
    }

    addEventHandlers(gameInstance){
        gameInstance.movablePlanet.on('pointerdown', function (pointer) {
            gameInstance.movablePlanetArc.visible = true;
            gameInstance.dirLine1.visible = true;
            gameInstance.dirLine2.visible = true;
            gameInstance.canClearLines = false;
          });

        gameInstance.movablePlanet.on('pointerout', function (pointer) {
            gameInstance.canClearLines = true;
        });
    }

    addBackground(gameInstance){
        let background = gameInstance.add.image(0, 0, 'sky').setOrigin(0);
        background.setScale(1.25);
    }

    setLabelAmount(label, amount) {
        label.amount = amount;
        label.label.text = "" + label.amount;
    }

    onAddUnitsAttempt(gameInstance): void {
        var hasResources = false;
        if (gameInstance.activeUnit == 'Army') {
          hasResources = gameInstance.moneyLabel.amount >= 500 && gameInstance.manPower.amount >= 10;
        }
        else if (gameInstance.activeUnit == 'Fleet') {
          hasResources = gameInstance.moneyLabel.amount >= 500 && gameInstance.manPower.amount >= 10 && gameInstance.alloys.amount >= 5;
        }
        if (!hasResources) { gameInstance.popupErrorMsg.visible = true; }
        else {
          if (gameInstance.activeUnit == 'Army') {
            gameInstance.setLabelAmount(gameInstance.moneyLabel, gameInstance.moneyLabel.amount - 500);
            gameInstance.setLabelAmount(gameInstance.manPower, gameInstance.manPower.amount - 10);
            let armyAmount = localStorage.getItem('ArmayAmount');
            let sum = parseInt(armyAmount);
            sum++;
            localStorage.setItem('ArmayAmount', sum.toString());
            gameInstance.refreshArmyUnits.call(gameInstance);
          }
          else if (gameInstance.activeUnit == 'Fleet') {
            gameInstance.setLabelAmount(gameInstance.moneyLabel, gameInstance.moneyLabel.amount - 500);
            gameInstance.setLabelAmount(gameInstance.manPower, gameInstance.manPower.amount - 10);
            gameInstance.setLabelAmount(gameInstance.alloys, gameInstance.alloys.amount - 5);
            let fleetAmount = localStorage.getItem('FleetAmount');
            let sum = parseInt(fleetAmount);
            sum++;
            localStorage.setItem('FleetAmount', sum.toString());        
            gameInstance.refreshFleetUnits(gameInstance);
          }
        }
    }

    refreshArmyUnits(gameInstance): void {
        gameInstance.activeUnit = 'Army';
        let armyAmount = localStorage.getItem('ArmayAmount');
        gameInstance.armyUnitLabel.text = gameInstance.activeUnit + ' Unit: ' + armyAmount;

        gameInstance.unitsIcon.setTexture('rect');
        gameInstance.unitsIconText.text = armyAmount;
        gameInstance.resForArmyContainer.visible = true;
    }

    refreshFleetUnits(gameInstance): void {
        gameInstance.activeUnit = 'Fleet';
        let fleetAmount = localStorage.getItem('FleetAmount');
        gameInstance.armyUnitLabel.text = gameInstance.activeUnit + ' Unit: ' + fleetAmount;
        gameInstance.unitsIcon.setTexture('pent');
        gameInstance.unitsIconText.text = fleetAmount;
        gameInstance.resForFleetContainer.visible = true;
    }

    makeLabelWithIcon(gameInstance, title, iconName, xPos, yPos, originX, originY) {
        var label = gameInstance.add.text(xPos, yPos, title, { fontFamily: 'Arial', fontSize: 18, color: '#ffffff' });;
        label.setOrigin(originX, originY);
        label.setScrollFactor(0);

        var icon = gameInstance.add.image(label.x - gameInstance.game.config.width * 0.060, label.y, iconName);
        icon.setScrollFactor(0);
        icon.setScale(0.4);
        icon.setOrigin(originX, originY);

        return { label: label, icon: icon };
    }

    addClockBar(gameInstance){
        gameInstance.leftBar = gameInstance.add.image(gameInstance.game.config.width * -0.230, gameInstance.moneyLabel.label.y - gameInstance.moneyLabel.label.y * 0.55, 'left_bar');
        gameInstance.leftBar.setScale(0.8);
        gameInstance.leftBar.setOrigin(0.15, 0.5);
        gameInstance.leftBar.setScrollFactor(0);

        gameInstance.clockIcon = gameInstance.add.image(gameInstance.game.config.width * 0.98, gameInstance.clockLabel.y, 'clock_icon');
        gameInstance.clockIcon.setScale(0.25);
        gameInstance.clockIcon.setOrigin(1, 0.5);
        gameInstance.clockIcon.setScrollFactor(0);

        gameInstance.clockBar = gameInstance.add.image(gameInstance.game.config.width * 1.15, gameInstance.clockLabel.y - gameInstance.clockLabel.y * 0.5, 'clock_bar');
        gameInstance.clockBar.setScale(0.8);
        gameInstance.clockBar.setOrigin(0.5, 0.5);
        gameInstance.clockBar.setScrollFactor(0);
    }

    makePanel(gameInstance, posX, posY, panelTitle, contentHeight, panelOriginX = 1, panelTitleOriginX = 0.5, onPointerDown = null) {
        var panelContainer = gameInstance.add.container(0, 0);

        var panel = gameInstance.add.image(posX, posY, 'quad');
        panel.setScale(7, contentHeight);
        panel.setOrigin(panelOriginX, 1);
        panel.setScrollFactor(0);
        panel.setTint(0xbfbfbf);

        var panelLabelBackground = gameInstance.add.image(posX, posY - panel.height * panel.scaleY, 'quad');
        panelLabelBackground.setScale(7, 1.2);
        panelLabelBackground.setOrigin(panelOriginX, 1);
        panelLabelBackground.setScrollFactor(0);
        panelLabelBackground.setTint(0x000000);
        panelLabelBackground.setInteractive();
        panelLabelBackground.panel = panelContainer;
        panelLabelBackground.on('pointerdown', function (pointer) {
          gameInstance.setTint(0x161616);
          var tween = gameInstance.scene.tweens.add
            ({
              targets: gameInstance.panel,
              y: gameInstance.panel.hide ? 0 : gameInstance.panel.hidePositionY,
              ease: 'Power1',
              duration: 400,
              repeat: 0,
            });

          gameInstance.panel.hide = !gameInstance.panel.hide;

          if (onPointerDown != null) {
            onPointerDown.call(this, pointer);
          }
        });
        panelLabelBackground.on('pointerup', function (pointer) {
          gameInstance.setTint(0x000000);
        });
        panelLabelBackground.on('pointerout', function (pointer) {
          gameInstance.setTint(0x000000);
        });

        var panelLabel = gameInstance.add.text(panelLabelBackground.x - panelLabelBackground.width * panelLabelBackground.scaleX * 0.5,
          panelLabelBackground.y,
          panelTitle, { fontFamily: 'Arial', fontSize: 24, color: '#ffffff' });

        panelLabel.setOrigin(panelTitleOriginX, 1.1);
        panelLabel.setScrollFactor(0);

        panelContainer.add(panel);
        panelContainer.add(panelLabelBackground);
        panelContainer.add(panelLabel);

        panelContainer.panel = panel;
        panelContainer.panelLabelBackground = panelLabelBackground;
        panelContainer.panelLabel = panelLabel;

        panelContainer.y += panel.height * panel.scaleY;
        panelContainer.hide = true;
        panelContainer.hidePositionY = panelContainer.y;

        panelContainer.nextItemOffsetY = 0;
        panelContainer.contentHeight = contentHeight;

        return panelContainer;
    }
}