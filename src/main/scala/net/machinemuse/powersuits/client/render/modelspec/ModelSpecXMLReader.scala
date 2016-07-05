package net.machinemuse.powersuits.client.render.modelspec

import scala.xml.{NodeSeq, XML}
import java.awt.Color

import net.machinemuse.utils.MuseStringUtils
import java.net.URL

import net.machinemuse.numina.general.MuseLogger
import net.machinemuse.numina.geometry.Colour
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.Vec3d


/**
 * Author: MachineMuse (Claire Semple)
 * Created: 8:44 AM, 4/28/13
 */
object ModelSpecXMLReader {
  def parseFile(file: URL) = {
    val xml = XML.load(file)
    (xml \\ "model") foreach {
      modelnode => parseModel(modelnode)
    }
  }

  def parseModel(modelnode: NodeSeq) = {
    val file = (modelnode \ "@file").text
    val textures = (modelnode \ "@textures").text.split(",")
    val offset = parseVector((modelnode \ "@offset").text)
    val rotation = parseVector((modelnode \ "@rotation").text)

    ModelRegistry.loadModel(new ResourceLocation(file)) match {
      case Some(m) => {
        val modelspec = new ModelSpec(m, textures, offset, rotation, file)
        val existingspec = ModelRegistry.put(MuseStringUtils.extractName(file), modelspec)
        (modelnode \ "binding").foreach {
          bindingnode => parseBinding(bindingnode, existingspec)
        }
      }
      case None => MuseLogger logError "Model file " + file + " not found! D:"
    }

  }

  def parseBinding(bindingnode: NodeSeq, modelspec: ModelSpec) = {
    val slot = parseArmorSlot((bindingnode \ "@slot").text)
    val target = parseTarget((bindingnode \ "@target").text)
    slot.foreach(slot => {
      target.foreach(target =>
        (bindingnode \ "part").foreach {
          partnode =>
            parseParts(partnode, modelspec, slot, target)
        })
    })
  }

  def parseParts(partNode: NodeSeq, modelspec: ModelSpec, slot: EntityEquipmentSlot, target: MorphTarget) = {
    val defaultcolor = parseColour((partNode \ "@defaultcolor").text)
    val defaultglow = parseBool((partNode \ "@defaultglow").text)
    val name = (partNode \ "@name").text
    val polygroup = validatePolygroup((partNode \ "@polygroup").text, modelspec)
    polygroup.map(polygroup => {
      val partspec = new ModelPartSpec(modelspec, target, polygroup, slot, 0, defaultglow.getOrElse(false), name)
      modelspec.put(polygroup, partspec)
    })
  }

  def validatePolygroup(s: String, m: ModelSpec): Option[String] = {
    //FIXME !!!!
//    val it = m.model.groupObjects.iterator
//    while (it.hasNext) {
//      if (it.next().name.equals(s)) return Some(s)
//    }
    return None
  }

  def parseBool(s: String): Option[Boolean] = {
    try Some(s.toBoolean) catch {
      case _: Throwable => None
    }
  }

  def parseColour(s: String): Option[Colour] = {
    try {
      val c = Color.decode(s)
      Some(new Colour(c.getRed, c.getGreen, c.getBlue, c.getAlpha))
    } catch {
      case _: Throwable => None
    }
  }

  def parseTarget(s: String): Option[MorphTarget] = {
    s.toLowerCase match {
      case "head" => Some(Head)
      case "body" => Some(Body)
      case "leftarm" => Some(LeftArm)
      case "rightarm" => Some(RightArm)
      case "leftleg" => Some(LeftLeg)
      case "rightleg" => Some(RightLeg)
//      case "cloak" => Some(Cloak)
      case _ => None
    }
  }

  def parseArmorSlot(s: String): Option[EntityEquipmentSlot] = {
    // FIXME

//    s.toUpperCase match {
//      case "FEET" => Some(EntityEquipmentSlot.FEET)
//      case "LEGS" => Some(EntityEquipmentSlot.LEGS)
//      case "CHEST" => Some(EntityEquipmentSlot.CHEST)
//      case "HEAD" => Some(EntityEquipmentSlot.HEAD)
//      case "MAINHAND" => Some(EntityEquipmentSlot.MAINHAND)
//      case "OFFHAND" => Some(EntityEquipmentSlot.OFFHAND)
//    }
    return Some(EntityEquipmentSlot.OFFHAND)
  }

  def parseInt(s: String): Option[Int] = {
    try Some(s.toInt) catch {
      case _: Throwable => None
    }
  }

  def parseVector(s: String): Option[Vec3d] = {
    try {
      val ss = s.split(",")
      val x = ss(0).toDouble
      val y = ss(1).toDouble
      val z = ss(2).toDouble
      Some(new Vec3d(x, y, z))
    } catch {
      case _: Throwable => None
    }
  }
}
