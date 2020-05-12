package stsjorbsmod.cards.cull;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;
import com.megacrit.cardcrawl.powers.IntangiblePower;
import com.megacrit.cardcrawl.relics.ChemicalX;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import stsjorbsmod.JorbsMod;
import stsjorbsmod.cards.CustomJorbsModCard;
import stsjorbsmod.characters.Cull;
import stsjorbsmod.powers.CoupDeGracePower;

import static stsjorbsmod.JorbsMod.JorbsCardTags.LEGENDARY;

public class CoupDeGrace extends CustomJorbsModCard {
    public static final String ID = JorbsMod.makeID(CoupDeGrace.class);

    private static final CardRarity RARITY = CardRarity.BASIC;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = Cull.Enums.CULL_CARD_COLOR;

    private static final int COST = CustomJorbsModCard.COST_X;
    private static final int BASE_EXTRA_PLAYS = 0;

    private boolean targetingEnemy = false;

    public CoupDeGrace() {
        super(ID, COST, TYPE, COLOR, RARITY, TARGET);
        magicNumber = baseMagicNumber = BASE_EXTRA_PLAYS;
        tags.add(LEGENDARY);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (AbstractDungeon.player.hasRelic(ChemicalX.ID)) {
            AbstractDungeon.player.getRelic(ChemicalX.ID).flash();
        }

        if (targetingEnemy && m != null) {
            addToBot(new ApplyPowerAction(m, m, new IntangiblePlayerPower(m, magicNumber)));
            addToBot(new ApplyPowerAction(m, m, new CoupDeGracePower(m, magicNumber)));
        }
        else if (!targetingEnemy) {
            addToBot(new ApplyPowerAction(p, p, new IntangiblePlayerPower(m, magicNumber)));
            addToBot(new ApplyPowerAction(p, p, new CoupDeGracePower(p, magicNumber)));
        }

        if (!this.freeToPlayOnce) {
            p.energy.use(EnergyPanel.totalCount);
        }
    }

    @Override
    public void update() {
        super.update();
        AbstractPlayer p = AbstractDungeon.player;

        if (p.isDraggingCard && p.hoveredCard.equals(this)) {
            AbstractCreature hoveredEnemy = null;
            for (AbstractCreature enemy : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (enemy.hb.hovered && !enemy.isDead && !enemy.halfDead) {
                    hoveredEnemy = enemy;
                }
            }
            if (hoveredEnemy != null) {
                if(!this.targetingEnemy) {
                    this.targetingEnemy = true;
                    p.inSingleTargetMode = true;
                    this.target = CardTarget.ENEMY;
                    this.target_x = hoveredEnemy.hb.cX - this.hb.width * 1.0F - hoveredEnemy.hb_w * 1.0F;
                    this.target_y = hoveredEnemy.hb.cY;
                    this.applyPowers();
                }
            }
            else {
                if(this.targetingEnemy) {
                    this.targetingEnemy = false;
                    p.inSingleTargetMode = false;
                    this.target = CardTarget.SELF;
                    this.applyPowers();
                }
            }
        }
    }

    @Override
    protected int calculateBonusMagicNumber() {
        int effect = EnergyPanel.totalCount;
        if (this.energyOnUse != -1) {
            effect = this.energyOnUse;
        }

        if (AbstractDungeon.player.hasRelic(ChemicalX.ID)) {
            effect += 2;
        }

        if (this.upgraded) {
            effect += 1;
        }

        return effect;
    }

    @Override
    public void upgrade() {
        if(!upgraded) {
            upgradeName();
            upgradeDescription();
        }
    }
}
