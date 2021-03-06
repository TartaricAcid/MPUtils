package com.gendeathrow.tipalert.client;

import java.awt.Color;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.gendeathrow.mputils.utils.RenderAssist;
import com.gendeathrow.tipalert.prompt.Tips.Tip;
import com.gendeathrow.tipalert.prompt.Tips.TipDimension;

@SideOnly(Side.CLIENT)
@Deprecated
public class TipsNotification extends Gui
{
	    private static final ResourceLocation tipsBg = new ResourceLocation("textures/gui/achievement/achievement_background.png");
	    private Minecraft mc;
	    private int width;
	    private int height;
	    private String tipTitle;
	    private String tipDescription;
	    private Tip theTip;
	    private long notificationTime;
	    private RenderItem renderItem;
	    private boolean permanentNotification;
	    
	    private ArrayList<Tip> queue = new ArrayList<Tip>();

	    public TipsNotification(Minecraft mc)
	    {
	        this.mc = mc;
	        this.renderItem = mc.getRenderItem();
	    }

	    public void displayTip(Tip tip)
	    {
	        this.tipTitle = tip.getTitle();
	        this.tipDescription = tip.getDescription();
	        this.notificationTime = Minecraft.getSystemTime();
	        this.theTip = tip;
	        this.permanentNotification = false;
	    }
	    
	    
	    public Tip getLastTip()
	    {
	    	return this.theTip;
	    }
	    
	    public void addQueue(Tip tip)
	    {
	    	if(tip.shouldNotifiy())
	    	{
	    		//System.out.println("add to queue"+ tip.toString());
	    		this.queue.add(tip);
	    	}
	    	else 
	    	{
	    		//System.out.println("last seend "+ TimeUnit.MILLISECONDS.toMinutes(Minecraft.getSystemTime() - tip.LastSeen()) +" ago. shows after 10mins");
	    		tip.lastSeen();
	    	}
	    }
	    
	    public boolean hasNextQueue()
	    {
	    	return this.queue.size() > 0 ? (this.queue.get(0) != null ? true : false) : false;
	    }
	    
	    public Tip getNextQueue()
	    {
	    	
	    	Tip tip = this.queue.get(0);
	    	
	    	if(tip != null)
	    	{
	    		this.queue.remove(0);
	    		return tip;
	    	}
	    	return null;
	    }
	    
	    public boolean isInQueue(Tip checkagainst)
	    {
	    	if(this.theTip == checkagainst) return true;
	    	else return this.queue.contains(checkagainst);
	    }

	    public void displayUnformattedTip(Tip tipIn)
	    {
	        this.tipTitle = tipIn.getTitle();
	        this.tipDescription = tipIn.getDescription();
	        this.notificationTime = Minecraft.getSystemTime() + 2500L;
	        this.theTip = tipIn;
	        this.permanentNotification = true;
	    }

	    private void updateTipWindowScale()
	    {
	        GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
	        GL11.glMatrixMode(GL11.GL_PROJECTION);
	        GL11.glLoadIdentity();
	        GL11.glMatrixMode(GL11.GL_MODELVIEW);
	        GL11.glLoadIdentity();
	        this.width = this.mc.displayWidth;
	        this.height = this.mc.displayHeight;
	        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
	        this.width = scaledresolution.getScaledWidth();
	        this.height = scaledresolution.getScaledHeight();
	        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
	        GL11.glMatrixMode(GL11.GL_PROJECTION);
	        GL11.glLoadIdentity();
	        GL11.glOrtho(0.0D, (double)this.width, (double)this.height, 0.0D, 1000.0D, 3000.0D);
	        GL11.glMatrixMode(GL11.GL_MODELVIEW);
	        GL11.glLoadIdentity();
	        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
	    }

	    public void updateTipWindow()
	    {
	        if (this.theTip != null && this.notificationTime != 0L && Minecraft.getMinecraft().thePlayer != null)
	        {
	        
	            int textHeight = this.mc.fontRendererObj.FONT_HEIGHT * this.mc.fontRendererObj.listFormattedStringToWidth(this.tipDescription, 120).size();
	        	
	            double d0 = (double)(Minecraft.getSystemTime() - this.notificationTime) / (6000.0D + (500 * (this.mc.fontRendererObj.listFormattedStringToWidth(this.tipDescription, 120).size() - 1 ))) ;
	            

	            if (!this.permanentNotification)
	            {
	                if (d0 < 0.0D || d0 > 1.0D)
	                {
	                    this.notificationTime = 0L;
	                    return;
	                }
	            }
	            else if (d0 > 0.5D)
	            {
	                d0 = 0.5D;
	            }

	            this.updateTipWindowScale();
	            GL11.glDisable(GL11.GL_DEPTH_TEST);
	            GL11.glDepthMask(false);
	            double d1 = d0 * 2.0D;

	            if (d1 > 1.0D)
	            {
	                d1 = 2.0D - d1;
	            }

	            d1 = d1 * 4.0D;
	            d1 = 1.0D - d1;

	            if (d1 < 0.0D)
	            {
	                d1 = 0.0D;
	            }

	            d1 = d1 * d1;
	            d1 = d1 * d1;

	            //	            int i = this.width - 160;
	            int i = 10;
	            int iOffset = 0;
	            
	            if(this.theTip instanceof TipDimension) 
	            { 
	            	iOffset = 18;
	            }
	            
	            int j = 0 - (int)(d1 * 36.0D);
	            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	            GL11.glEnable(GL11.GL_TEXTURE_2D);
	            this.mc.getTextureManager().bindTexture(tipsBg);
	            GL11.glDisable(GL11.GL_LIGHTING);
	            
	            
	            //RenderAssist.drawTexturedModalRectCustomSize(i, j, 96, 202, 160, 32, 160, textHeight + 30);
	            
	            drawRect(i, j, 160, textHeight + 30 + j, Color.black.getRGB());
	            RenderAssist.drawUnfilledRect(i, j, 160, textHeight + 30 + j, Color.WHITE.getRGB());

	            if (this.permanentNotification)
	            {
	            	this.mc.fontRendererObj.drawSplitString(this.tipDescription, i + 15, (int) (j + (textHeight * 0.21875)), 140, -1);
	            }
	            else
	            {
	                this.mc.fontRendererObj.drawString(this.tipTitle, i + 30, j + 7, -256);
	                this.mc.fontRendererObj.drawSplitString(this.tipDescription, i - iOffset + 30, (int) (j + (textHeight * 0.21875)) + 18, 120 + iOffset, -1);
	            }

	            RenderHelper.enableGUIStandardItemLighting();
	            GL11.glDisable(GL11.GL_LIGHTING);
	            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	            GL11.glEnable(GL11.GL_COLOR_MATERIAL);
	            GL11.glEnable(GL11.GL_LIGHTING);
            		this.renderItem.renderItemAndEffectIntoGUI(this.theTip.theItemStack, i + 8, j + 8);
	            GL11.glDisable(GL11.GL_LIGHTING);
	            GL11.glDepthMask(true);
	            GL11.glEnable(GL11.GL_DEPTH_TEST);
	        }else if(hasNextQueue())
	        {
                	this.displayTip(getNextQueue()); 
	        }
	    }

	    public void clearAchievements()
	    {
	        this.theTip = null;
	        this.notificationTime = 0L;
	    }
	}